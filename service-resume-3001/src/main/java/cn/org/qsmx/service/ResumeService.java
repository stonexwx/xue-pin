package cn.org.qsmx.service;

import cn.org.qsmx.pojo.Resume;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;


public interface ResumeService{
    /**
     * 用户注册的时候初始化简历
     * @param userId
     */
    void initResume(String userId);
    /**
     * 用户注册的时候初始化简历
     * @param userId
     */
    void initResume(String userId,String msgId);
}
