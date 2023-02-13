package cn.org.qsmx.service;

import cn.org.qsmx.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
public interface UsersService extends IService<Users> {
    /**
     * 判断用户是否存在，如果存在则返回用户信息，否则null
     * @param mobile
     * @return
     */
    Users queryMobileExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     * @param mobile
     * @return
     */
    Users createUsers(String mobile);


}
