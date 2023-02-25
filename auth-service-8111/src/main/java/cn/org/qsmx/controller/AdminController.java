package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.intercept.JWTCurrentInterceptor;
import cn.org.qsmx.pojo.Admin;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.bo.AdminBO;
import cn.org.qsmx.pojo.bo.RegistLoginBO;
import cn.org.qsmx.pojo.vo.AdminVO;
import cn.org.qsmx.pojo.vo.UserVO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.AdminService;
import cn.org.qsmx.service.UsersService;
import cn.org.qsmx.util.IPUtil;
import cn.org.qsmx.util.JWTUtil;
import cn.org.qsmx.util.SMSUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("admin")
@Slf4j
public class AdminController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("login")
    public GraceJSONResult getSMSCode(@Valid @RequestBody AdminBO adminBO){
        //判断用户是否存在
        boolean isExist = adminService.adminLogin(adminBO);
        if(!isExist){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_LOGIN_ERROR);
        }

        //登录之后获取用户信息
        Admin admin = adminService.getAdminInfo(adminBO);
        String adminToken = jwtUtil.createJWTPrefix(new Gson().toJson(admin), TOKEN_ADMIN_PREFIX);

        return GraceJSONResult.ok(adminToken);
    }

    @GetMapping("info")
    public GraceJSONResult info(){
        Admin admin = JWTCurrentInterceptor.currentAdmin.get();

        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin,adminVO);

        return GraceJSONResult.ok(adminVO);
    }
    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId,HttpServletRequest request){
        return GraceJSONResult.ok();
    }
}
