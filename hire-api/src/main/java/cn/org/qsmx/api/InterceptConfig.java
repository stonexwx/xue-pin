package cn.org.qsmx.api;

import cn.org.qsmx.intercept.JWTCurrentInterceptor;
import cn.org.qsmx.intercept.SMSInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class InterceptConfig implements WebMvcConfigurer {

    /**
     * springboot放入拦截器
     * @return
     */
    @Bean
    public SMSInterceptor smsInterceptor() {
        return new SMSInterceptor();
    }

    @Bean
    public JWTCurrentInterceptor jwtCurrentInterceptor() {
        return new JWTCurrentInterceptor();
    }

    /**
     * 注册拦截器，拦截指定路由
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(jwtCurrentInterceptor())
                .addPathPatterns("/**");

    }
}
