package cn.org.qsmx.pojo.bo;

import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.pojo.Password;
import cn.org.qsmx.pojo.ar.AdminAR;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResetPwdBO {

    private String adminId;

    private Password password;

    private void checkAdminId(){
        if (StringUtils.isBlank(adminId)) {
            GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIST);
        }
        AdminAR adminAR =new AdminAR();
        adminAR.setId(adminId);
        adminAR = adminAR.selectById();
        if(adminAR==null)GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIST);
    }

    public void modifyPassword(){
        password.validate();
        checkAdminId();

        AdminAR adminAR =new AdminAR();
        adminAR.setId(adminId);

        String slat = (int)((Math.random() *9 +1)*100000)+"";
        String pwd = MD5Utils.encrypt(password.getPassword(),slat);
        adminAR.setPassword(pwd);
        adminAR.setSlat(slat);

        adminAR.setUpdatedTime(LocalDateTime.now());
        adminAR.insertOrUpdate();
    }

}
