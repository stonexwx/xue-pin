package cn.org.qsmx.controller;

import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 初始化用户简历
     * @param userId
     * @return
     */
    @PostMapping("init")
    public GraceJSONResult init(@RequestParam("userId") String userId) {
        resumeService.initResume(userId);

        return GraceJSONResult.ok();
    }
}
