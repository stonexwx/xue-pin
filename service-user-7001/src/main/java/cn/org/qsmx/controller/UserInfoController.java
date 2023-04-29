package cn.org.qsmx.controller;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.intercept.JWTCurrentInterceptor;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.pojo.bo.ModifyUserBO;
import cn.org.qsmx.pojo.vo.UserVO;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.service.UserService;
import cn.org.qsmx.util.JWTUtil;
import cn.org.qsmx.util.SMSUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("userinfo")
@Slf4j
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;
    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO userBO) throws Exception {

        //修改用户信息
        userService.modifyUserInfo(userBO);

        //返回用户信息
        UserVO userVO = getUserInfo(userBO.getUserId());
        return GraceJSONResult.ok(userVO);
    }

    private UserVO getUserInfo(String userId) {

        //查询用户信息
        Users latestUser =userService.getUserById(userId);
        //重新生成token，覆盖原来的用户token
        String uToken = jwtUtil.createJWTPrefix(new Gson().toJson(latestUser), TOKEN_USER_PREFIX);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(latestUser, userVO);
        userVO.setUserToken(uToken);

        return userVO;
    }
}
