package cn.org.qsmx.service.impl;

import cn.org.qsmx.mapper.MqLocalMsgMapper;
import cn.org.qsmx.pojo.MqLocalMsg;
import cn.org.qsmx.service.MqLocalMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xwx
 * @since 2023-03-21
 */
@Service
public class MqLocalMsgServiceImpl extends ServiceImpl<MqLocalMsgMapper, MqLocalMsg> implements MqLocalMsgService {
    @Autowired
    private MqLocalMsgMapper mqLocalMsgMapper;

    @Override
    public List<MqLocalMsg> getBatchLocalMsg(List<String> msgIds) {

        return mqLocalMsgMapper.selectBatchIds(msgIds);
    }
}
