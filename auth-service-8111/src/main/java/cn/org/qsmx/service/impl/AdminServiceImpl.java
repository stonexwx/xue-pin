package cn.org.qsmx.service.impl;

import cn.org.qsmx.mapper.AdminMapper;
import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.bo.AdminBO;
import cn.org.qsmx.service.AdminService;
import cn.org.qsmx.util.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Override
    public boolean adminLogin(AdminBO adminBO) {

        //根据用户名获取盐
        Admin admin = getSelfAdmin(adminBO.getUsername());

        if(admin == null){
            return false;
        }else {
            String slat = admin.getSlat();
            String md5Str = MD5Utils.encrypt(adminBO.getPassword(),slat);
            return md5Str.equalsIgnoreCase(admin.getPassword());
        }
    }

    @Override
    public Admin getAdminInfo(AdminBO adminBO) {


        return getSelfAdmin(adminBO.getUsername());
    }

    private Admin getSelfAdmin(String username){
        return adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username",username)
        );
    }
}
