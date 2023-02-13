package cn.org.qsmx.intercept;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.IPUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JWTCurrentInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    public static ThreadLocal<Users> currentUser = new ThreadLocal<>();
    public static ThreadLocal<Admin> currentAdmin = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        //使用ThreadLocal可以在同一个线程内共享数据
        //比如：Interceptor --> Controller --> Service -> Mapper
        String appUserJson = request.getHeader(APP_USER_JSON);
        String sassUserJson = request.getHeader(SAAS_USER_JSON);
        String adminUserJson = request.getHeader(ADMIN_USER_JSON);

        if(StringUtils.isNotBlank(appUserJson) || StringUtils.isNotBlank(sassUserJson)){
            Users appUser = new Gson().fromJson(appUserJson,Users.class);
            currentUser.set(appUser);
            log.info(String.valueOf(currentUser.get()));
        }
        if(StringUtils.isNotBlank(adminUserJson)){
            Admin admin  = new Gson().fromJson(adminUserJson,Admin.class);
            currentAdmin.set(admin);
        }

        /*
          false:请求拦截
          true：请求通过
         */
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        currentUser.remove();
        currentAdmin.remove();
    }
}
