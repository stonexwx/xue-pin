package cn.org.qsmx.service;

import cn.org.qsmx.pojo.MqLocalMsg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xwx
 * @since 2023-03-21
 */
public interface MqLocalMsgService extends IService<MqLocalMsg> {
    /**
     * 批量获取本地消息列表
     * @param msgIds
     * @return
     */
    List<MqLocalMsg> getBatchLocalMsg(List<String> msgIds);
}
