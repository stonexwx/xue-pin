package cn.org.qsmx.controller;

import cn.org.qsmx.util.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("a")
@Slf4j
public class HelloController {
    @Autowired
    private SMSUtils smsUtils;
    @GetMapping("hello")
    public Object hello() throws Exception {
        return "Hello";
    }
}
