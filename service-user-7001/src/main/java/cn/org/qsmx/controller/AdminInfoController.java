package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.intercept.JWTCurrentInterceptor;
import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.ar.AdminAR;
import cn.org.qsmx.pojo.bo.AdminBO;
import cn.org.qsmx.pojo.bo.CreateAdminBO;
import cn.org.qsmx.pojo.bo.ResetPwdBO;
import cn.org.qsmx.pojo.bo.UpdateAdminBO;
import cn.org.qsmx.pojo.vo.AdminInfoVO;
import cn.org.qsmx.pojo.vo.AdminVO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.AdminService;
import cn.org.qsmx.util.JWTUtil;
import cn.org.qsmx.util.PagedGridResult;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("admininfo")
@Slf4j
public class AdminInfoController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;


    @PostMapping("create")
    public GraceJSONResult create(@Valid @RequestBody CreateAdminBO createAdminBO){
        adminService.createAdmin(createAdminBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public GraceJSONResult list(String accountName, Integer page,Integer limit){
        if (page == null) page = 1;
        if (limit == null) limit =10;

        PagedGridResult pagedGridResult = adminService.getAdminList(accountName,page,limit);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String username){

        adminService.deleteAdmin(username);

        return GraceJSONResult.ok();
    }

    @PostMapping("resetPwd")
    public GraceJSONResult resetPwd(@RequestBody ResetPwdBO resetPwdBO){

        resetPwdBO.modifyPassword();
        return GraceJSONResult.ok();
    }

    @PostMapping("myInfo")
    public GraceJSONResult myInfo(){

        Admin admin = JWTCurrentInterceptor.currentAdmin.get();
        Admin adminInfo = adminService.getById(admin.getId());
        AdminInfoVO adminInfoVO  = new AdminInfoVO();
        BeanUtils.copyProperties(adminInfo,adminInfoVO);
        return GraceJSONResult.ok(adminInfoVO);
    }

    @PostMapping("updateMyInfo")
    public GraceJSONResult updateMyInfo(@RequestBody UpdateAdminBO adminBO){

        Admin admin = JWTCurrentInterceptor.currentAdmin.get();

        adminBO.setId(admin.getId());
        log.info("adminBO:{}",adminBO);
        adminService.updateAdmin(adminBO);

        return GraceJSONResult.ok();
    }
}
