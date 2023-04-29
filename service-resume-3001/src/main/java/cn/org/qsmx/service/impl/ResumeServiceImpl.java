package cn.org.qsmx.service.impl;

import cn.org.qsmx.mapper.ResumeMapper;
import cn.org.qsmx.pojo.Resume;
import cn.org.qsmx.service.MqLocalMsgService;
import cn.org.qsmx.service.ResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class ResumeServiceImpl implements ResumeService {
    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private MqLocalMsgService mqLocalMsgService;
    @Transactional
    @Override
    public void initResume(String userId) {
        Resume resume = new Resume();

        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.insert(resume);
    }

    @Transactional
    @Override
    public void initResume(String userId,String msgId) {
        Resume resume = new Resume();

        resume.setUserId(userId);
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdatedTime(LocalDateTime.now());

        resumeMapper.insert(resume);

        // 最终一致性测试：removeId最后一位如果是单数，则抛出异常
        String resumeId = resume.getId();
        String tail = resumeId.substring(resumeId.length() - 1);
        Integer tailNum = Integer.valueOf(tail);
        if (tailNum % 2 == 0) {
            log.info("简历初始化成功。。。");
            //删除对应的本地消息记录表
            log.info("本地消息删除 tailNum为{}, id:{}",tailNum,msgId);
            mqLocalMsgService.removeById(msgId);
        }else {
            log.info("简历初始化失败 tailNum为{}, id:{}",tailNum,msgId);
            throw new RuntimeException("简历初始化失败。。。");
        }
    }
}
