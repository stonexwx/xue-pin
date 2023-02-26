package cn.org.qsmx.AOP;


import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class ServiceLogAspect {

    @Around("execution(* cn.org.qsmx.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint ) throws Throwable{

        long begin = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long takeTime = end - begin;
        if(takeTime > 500){
            log.error("执行时间太长了{}",takeTime);
        } else if (takeTime > 200) {
            log.error("执行时间稍微有点长{}",takeTime);
        }else {
            log.info("执行时间{}",takeTime);
        }
        return proceed;
    }
}
