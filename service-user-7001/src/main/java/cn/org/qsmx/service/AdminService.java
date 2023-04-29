package cn.org.qsmx.service;

import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.bo.CreateAdminBO;
import cn.org.qsmx.pojo.bo.UpdateAdminBO;
import cn.org.qsmx.util.PagedGridResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
public interface AdminService  {

    /**
     * 创建Admin账号
     * @param createAdminBO
     */
    void createAdmin(CreateAdminBO createAdminBO);

    /**
     * 查询admin列表
     *
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    PagedGridResult getAdminList(String accountName, Integer page,Integer limit);

    /**
     * 删除账号
     * @param accountName
     */
    void deleteAdmin(String userName);

    /**
     * 根据id查询admin
     * @param adminID
     * @return
     */
    Admin getById(String adminID);

    /**
     * 更新admin
     * @param adminBo
     */
    void updateAdmin(UpdateAdminBO adminBo);
}
