package cn.org.qsmx.service;

import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.bo.AdminBO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
public interface AdminService extends IService<Admin> {

    /**
     * admin登录
     * @param adminBO
     * @return
     */
    boolean adminLogin(AdminBO adminBO);

    /**
     * 获得admin信息
     * @param adminBO
     * @return
     */
    Admin getAdminInfo(AdminBO adminBO);
}
