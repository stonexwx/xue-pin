package cn.org.qsmx.intercept;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.exceptions.MyCustomException;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SMSInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        //获取用户ip
        String userIP= IPUtil.getRequestIp(request);
        boolean ipExists = redis.keyIsExist(MOBILE_SMSCODE+":"+userIP);
        if(ipExists){
            log.info("短信发送频率较高");
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        /*
          false:请求拦截
          true：请求通过
         */
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
