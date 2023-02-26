package cn.org.qsmx.pojo;

import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.result.ResponseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Password {
    private String password;
    private String rePassword;

    public void validate(){
        checkPwd();
    }

    private void checkPwd(){
        if(StringUtils.isBlank(password)) GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        if(StringUtils.isBlank(rePassword)) GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
        if(!password.equals(rePassword)) GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
    }
}
