package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.bo.RegistLoginBO;
import cn.org.qsmx.pojo.vo.UserVO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.UsersService;
import cn.org.qsmx.util.IPUtil;
import cn.org.qsmx.util.JWTUtil;
import cn.org.qsmx.util.SMSUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtil jwtUtil;

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
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ+":"+qrToken,"1"+preToken,5*60);
        return GraceJSONResult.ok(preToken);
    }
}
