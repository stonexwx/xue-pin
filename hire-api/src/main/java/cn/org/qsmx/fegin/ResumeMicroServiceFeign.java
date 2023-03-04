package cn.org.qsmx.fegin;

import cn.org.qsmx.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("resume-service") //告诉SpringCloud，本接口为调用远程服务Service
public interface ResumeMicroServiceFeign {

    @PostMapping("/resume/init")
    public GraceJSONResult init(@RequestParam("userId") String userId);
}
