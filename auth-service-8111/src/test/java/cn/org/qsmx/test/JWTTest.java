package cn.org.qsmx.test;

import cn.org.qsmx.pojo.Stu;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;

@SpringBootTest
public class JWTTest {
    //定义秘钥
    public static final String USER_KEY ="xwx_1jjkhiufhuhijfsdnb_28897392847";
    @Test
    public void createJwt(){
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        Stu stu = new Stu(1001,"wxw",18);
        String stuJson = new Gson().toJson(stu);
        String myJwt = Jwts.builder()
                .setSubject(stuJson)        //设置用户自定义数据
                .signWith(secretKey)        //使用那个密钥对象进行JWT的生成
                .compact();                 //压缩生成JWT
        System.out.println(myJwt);
    }
    @Test
    public void checkJwt(){
        String base64 = new BASE64Encoder().encode(USER_KEY.getBytes());
        String jwtString ="eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ7XCJpZFwiOjEwMDEsXCJuYW1lXCI6XCJ3eHdcIixcImFnZVwiOjE4fSJ9.a3H2npBqM0u0HjD98p2c2y8rd0vBg_DSxJTcARYt9PdaNU0j6aLiFHU-r97kbW_Q";
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        Jws<Claims> jws = jwtParser.parseClaimsJws(jwtString);
        String stujson = jws.getBody().getSubject();
        Stu stu = new Gson().fromJson(stujson, Stu.class);
        System.out.println(stu);
    }
}
