package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.intercept.JWTCurrentInterceptor;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.util.SMSUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("u")
@Slf4j
public class HelloController extends BaseInfoProperties {
    @Autowired
    private SMSUtils smsUtils;
    @GetMapping("hello")
    public Object hello(HttpServletRequest request) throws Exception {
        Users userJson = JWTCurrentInterceptor.currentUser.get();
        log.info(String.valueOf(userJson));
        return userJson.toString();
    }
}
