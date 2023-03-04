package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.mq.RabbitMQSMSConfig;
import cn.org.qsmx.pojo.QO.SMSContentQO;
import cn.org.qsmx.pojo.bo.RegistLoginBO;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.vo.UserVO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.UsersService;
import cn.org.qsmx.util.GsonUtils;
import cn.org.qsmx.util.IPUtil;
import cn.org.qsmx.util.JWTUtil;
import cn.org.qsmx.util.SMSUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("passport")
@Slf4j
public class PassportController extends BaseInfoProperties {
    @Autowired
    private SMSUtils smsUtils;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UsersService usersService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) throws Exception {
        if(StringUtils.isBlank(mobile)){
            return GraceJSONResult.error();
        }

        //获取用户ip
        String userIP= IPUtil.getRequestIp(request);
        //限制用户只能60s以内获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE+":"+userIP,mobile);
        String code = (int)((Math.random() *9+1)*100000)+"";
//        smsUtils.sendSMS(mobile,code);

        //使用消息队列异步解耦发送短信
        SMSContentQO contentQO = new SMSContentQO();
        contentQO.setMobile(mobile);
        contentQO.setContent(code);

//        rabbitTemplate.convertAndSend(RabbitMQSMSConfig.SMS_EXCHANGE,
//                RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN,
//                GsonUtils.object2String(contentQO));


        //把验证码存入redis，用于后期注册登录
        redis.set(MOBILE_SMSCODE+":"+mobile,code,30*60);
        log.info("验证码为：{}",code);
        return GraceJSONResult.ok();
    }

    @PostMapping("login")
    public GraceJSONResult getSMSCode(@Valid @RequestBody RegistLoginBO registLoginBO
                                      ,HttpServletRequest request) throws Exception{
        String mobile = registLoginBO.getMobile();
        String code = registLoginBO.getSmsCode();
        String redisCode = redis.get(MOBILE_SMSCODE+":"+mobile);
        if(StringUtils.isBlank(redisCode)|| !redisCode.equalsIgnoreCase(code)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        Users user  = usersService.queryMobileExist(mobile);
        if(user==null){
            user=usersService.createUsers(mobile);
        }

//        String uToken = TOKEN_USER_PREFIX+SYMBOL_DOT+ UUID.randomUUID();
//        redis.set(REDIS_ADMIN_TOKEN+":"+user.getId(),uToken);
        String jwt = jwtUtil.createJWTPrefix(new Gson().toJson(user), 1000L,TOKEN_USER_PREFIX);

        redis.del(MOBILE_SMSCODE+":"+mobile);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        userVO.setUserToken(jwt);
        return GraceJSONResult.ok(userVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId,HttpServletRequest request){
        redis.del(REDIS_ADMIN_TOKEN+":"+userId);

        return GraceJSONResult.ok();
    }
}
