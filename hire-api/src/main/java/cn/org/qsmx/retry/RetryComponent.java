package cn.org.qsmx.retry;

import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class RetryComponent {

    @Autowired
    private SMSUtils smsUtils;

    @Retryable(maxAttempts = 5, //重试总次数
            backoff = @Backoff(delay = 1000L,multiplier = 2)//重试间隔为1s，后续重试次数的2倍
    )
    public boolean sendSmsWithRetry(String phone, String code){
        log.info("当前时间{}", LocalDateTime.now());
        return smsUtils.sendSMS(phone,code);
    }

    //达到最大重试次数，抛出一个异常
    @Recover
    public boolean recover(){
        GraceException.display(ResponseStatusEnum.SYSTEM_SMS_FALLBACK_ERROR);
        return false;
    }
}
