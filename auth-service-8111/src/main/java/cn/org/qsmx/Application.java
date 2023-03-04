package cn.org.qsmx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient //开启服务的注册发现功能
@MapperScan("cn.org.qsmx.mapper")
@EnableFeignClients("cn.org.qsmx.fegin")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
