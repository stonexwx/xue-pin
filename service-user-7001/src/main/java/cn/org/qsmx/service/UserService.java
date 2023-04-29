package cn.org.qsmx.service;

import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.bo.CreateAdminBO;
import cn.org.qsmx.pojo.bo.ModifyUserBO;
import cn.org.qsmx.util.PagedGridResult;

/**
 * <p>
 * 学聘网用户信息
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
public interface UserService {

    /**
     * 修改用户信息
     * @param userBO
     */
    void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    Users getUserById(String userId);
}
