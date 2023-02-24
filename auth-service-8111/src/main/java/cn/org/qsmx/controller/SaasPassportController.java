package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.bo.RegistLoginBO;
import cn.org.qsmx.pojo.vo.SaasUserVO;
import cn.org.qsmx.pojo.vo.UserVO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.UsersService;
import cn.org.qsmx.util.IPUtil;
import cn.org.qsmx.util.JWTUtil;
import cn.org.qsmx.util.SMSUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UsersService usersService;

    /**
     * 生成二维码登录token
     * @return
     */
    @PostMapping("getQRToken")
    public GraceJSONResult gerQRToken(){

        //生产扫码登录的token
        String qrToken = UUID.randomUUID().toString();
        //把QRToken存入到redis，设置一定时效，默认二维码超时，啧需要刷新后再次获得新的二维码
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN+":"+qrToken,qrToken,5*60);
        //存入redis标记当前的qrToken未被扫描读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ+":"+qrToken,"0",5*60);
        //返回给前端
        return GraceJSONResult.ok(qrToken);
    }

    /**
     * 手机端使用HR角色进行扫码操作
     * @param qrToken
     * @param request
     * @return
     */
    @PostMapping("scanCode")
    public GraceJSONResult scanCode(String qrToken,HttpServletRequest request){
        if(StringUtils.isEmpty(qrToken)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }

        String redisQRtToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN+":"+qrToken);
        if (!redisQRtToken.equalsIgnoreCase(qrToken)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }

        String headerUserId = request.getHeader("appUserId");
        String headerUserToken = request.getHeader("appUserToken");

        if(StringUtils.isBlank(headerUserId) || StringUtils.isBlank(headerUserToken)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }

        String userJson = jwtUtil.checkJWT(headerUserToken.split("@")[1]);
        if (StringUtils.isBlank(userJson)){
            return  GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }

        //生成预登陆token
        String preToken = UUID.randomUUID().toString();
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN+":"+qrToken,preToken,5*60);

        //redis写入标记，当前qeToken失效，
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ+":"+qrToken,"1,"+preToken,5*60);
        return GraceJSONResult.ok(preToken);
    }

    /**
     * 前端每隔一段时间定时查询qeToken是否被读取，用于页面的展示标记判断
     * @param qrToken
     * @return
     */
    @PostMapping("codeHasBeenRead")
    public GraceJSONResult codeHasBeenRead(String qrToken){
        String readStr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ+":"+qrToken);
        List list = new ArrayList();

        if(StringUtils.isNotBlank(readStr)){
            String[] readStrArray = readStr.split(",");
            if(readStrArray.length>=2){
                list.add(Integer.valueOf(readStrArray[0]));
                list.add(readStrArray[1]);
            }else {
                list.add(0);
            }
            return GraceJSONResult.ok(list);
        }else {
            return GraceJSONResult.ok(list);
        }
    }

    /**
     * 手机端点击登录，携带preToken与后端进行判断，校验成功就能登录
     * @param userId
     * @param qrToken
     * @param preToken
     * @return
     */
    @PostMapping("goQRLogin")
    public GraceJSONResult goQRLogin(String userId,String qrToken,String preToken){
        String preTokenRedisArr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ+":"+qrToken);

        if (StringUtils.isNotBlank(preTokenRedisArr)){
            String preTokenRedis = preTokenRedisArr.split(",")[1];
            if (preTokenRedis.equalsIgnoreCase(preToken)){
                Users hrUser = usersService.getById(userId);
                if(hrUser == null){
                    return  GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
                }

                //存入用户信息到redis,因为H5在未登陆的情况下，拿不到用户id
                redis.set(REDIS_SAAS_USER_INFO+":temp"+preToken,new Gson().toJson(hrUser),5*60);

            }
        }
        return GraceJSONResult.ok();
    }

    /**
     * 登录页面跳转
     * @param preToken
     * @return
     */
    @PostMapping("CheckLogin")
    public GraceJSONResult CheckLogin(String preToken){

        if (StringUtils.isBlank(preToken)){
            return GraceJSONResult.error();
        }

        String userJson = redis.get(REDIS_SAAS_USER_INFO+":temp"+preToken);

        if (StringUtils.isBlank(userJson)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        String saasUserToken = jwtUtil.createJWTPrefix(userJson,TOKEN_SAAS_PREFIX);
        redis.set(REDIS_SAAS_USER_INFO+":"+saasUserToken,userJson);

        return GraceJSONResult.ok(saasUserToken);
    }

    @GetMapping("info")
    public GraceJSONResult info(String token){
        String userJson = redis.get(REDIS_SAAS_USER_INFO+":"+token);

        Users users = new Gson().fromJson(userJson,Users.class);

        SaasUserVO saasUserVO = new SaasUserVO();
        BeanUtils.copyProperties(users,saasUserVO);

        return GraceJSONResult.ok(saasUserVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(String token){
        return GraceJSONResult.ok();
    }

}
