package cn.org.qsmx.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {

    @Test
    public void testPwdEncrypt(){
        //实例化加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

        //配置加密算法和秘钥
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword("");
        config.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setConfig(config);

        //对密码加密
        String myPwd = "";
        String encryptedPwd = encryptor.encrypt(myPwd);
        System.out.println(encryptedPwd);
    }
}
