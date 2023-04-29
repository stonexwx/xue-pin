package cn.org.qsmx.service.impl;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.mapper.AdminMapper;
import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.bo.CreateAdminBO;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.AdminService;
import cn.org.qsmx.util.MD5Utils;
import cn.org.qsmx.util.PagedGridResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 学聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
@Service
public class AdminServiceImpl extends BaseInfoProperties implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Transactional
    @Override
    public void createAdmin(CreateAdminBO createAdminBO) {
        //admin账号判断是否存在，如果存在，不予创建
        Admin admin = getSelfAdmin(createAdminBO.getUsername());
        if(admin!=null){
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }

        Admin newadmin = new Admin();
        BeanUtils.copyProperties(createAdminBO,newadmin);

        String slat = (int)((Math.random()*9+1)*10000)+"";
        String pwd = MD5Utils.encrypt(createAdminBO.getPassword(),slat);
        newadmin.setPassword(pwd);
        newadmin.setSlat(slat);

        newadmin.setUpdatedTime(LocalDateTime.now());
        newadmin.setCreateTime(LocalDateTime.now());

        adminMapper.insert(newadmin);
    }

    @Override
    public PagedGridResult getAdminList(String accountName, Integer page, Integer limit) {

        PageHelper.startPage(page,limit);

        List<Admin> adminList = adminMapper.selectList(
                new QueryWrapper<Admin>()
                        .like("username",accountName)
        );

        return setterPagedGrid(adminList,page) ;
    }

    @Override
    public void deleteAdmin(String userName) {
        int res = adminMapper.delete(new QueryWrapper<Admin>()
                .eq("username",userName)
                .ne("username","admin")
        );
        if (res == 0) GraceException.display(ResponseStatusEnum.DATA_DICT_DELETE_ERROR);
    }

    @Override
    public Admin getById(String adminID) {
        return adminMapper.selectById(adminID);
    }

    private Admin getSelfAdmin(String username){
        return adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username",username)
        );
    }
}
