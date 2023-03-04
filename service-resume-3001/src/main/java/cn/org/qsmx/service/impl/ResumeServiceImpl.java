package cn.org.qsmx.service.impl;

import cn.org.qsmx.mapper.ResumeMapper;
import cn.org.qsmx.pojo.Resume;
import cn.org.qsmx.service.ResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 简历表 服务实现类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private ResumeMapper resumeMapper;
    @Transactional
    @Override
    public void initResume(String userId) {
        Resume resume = new Resume();

        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.insert(resume);
    }
}
