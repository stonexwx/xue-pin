package cn.org.qsmx.service.impl;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.mapper.AdminMapper;
import cn.org.qsmx.mapper.UsersMapper;
import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.bo.CreateAdminBO;
import cn.org.qsmx.pojo.bo.ModifyUserBO;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.AdminService;
import cn.org.qsmx.service.UserService;
import cn.org.qsmx.util.MD5Utils;
import cn.org.qsmx.util.PagedGridResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学聘网运营管理系统的用户信息
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
@Service
public class UserServiceImpl extends BaseInfoProperties implements UserService {

    @Autowired
    private UsersMapper usersMapper;
    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {
        String userId = userBO.getUserId();
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }

        Users pendingUser = new Users();
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(userBO, pendingUser);

        usersMapper.updateById(pendingUser);
    }

    @Override
    public Users getUserById(String userId) {
        return usersMapper.selectById(userId);
    }
}
