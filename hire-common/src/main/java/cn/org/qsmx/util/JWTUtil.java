package cn.org.qsmx.util;

import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.result.ResponseStatusEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
@RefreshScope
public class JWTUtil {
    public static final String at = "@";
    @Autowired
    private JWTProperties jwtProperties;

    @Value("${jwt.key}")
    public String JWT_KEY;

    public String createJWTPrefix(String body,Long expireTime,String prefix){
        if(expireTime == null){
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return prefix + at + createJWT(body,expireTime);
    }
    public String createJWTPrefix(String body,String prefix){
        return prefix + at + createJWT(body);
    }

    public String createJWT(String body){
        return  delJwt(body,null);
    }
    public String createJWT(String body,Long expireTime){
        if(expireTime == null){
            GraceException.display(ResponseStatusEnum.SYSTEM_NO_EXPIRE_ERROR);
        }
        return delJwt(body,expireTime);
    }
    public String delJwt(String body,Long expireTime){
        String base64 = new BASE64Encoder().encode(JWT_KEY.getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        String jwt ="";
        if(expireTime!=null){
            jwt=generatorJWT(body,expireTime,secretKey);
        }else {
            jwt = generatorJWT(body,secretKey);
        }
        log.info(jwt);
        return jwt;
    }
    public String generatorJWT(String body, SecretKey secretKey){
        return Jwts.builder()
                .setSubject(body)        //???????????????????????????
                .signWith(secretKey)        //??????????????????????????????JWT?????????
                .compact();
    }
    public String generatorJWT(String body,Long expireTime , SecretKey secretKey){
        //??????????????????
        Date expireDate = new Date(System.currentTimeMillis()+expireTime);
        return Jwts.builder()
                .setSubject(body)        //???????????????????????????
                .signWith(secretKey)        //??????????????????????????????JWT?????????
//                .setExpiration(expireDate)
                .compact();
    }
    public String checkJWT(String pendingJWT){
        String userKey = JWT_KEY;

        //1. ???????????????base64?????????
        String base64 = new BASE64Encoder().encode(userKey.getBytes());

        //2. ???base64???????????????????????????
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());

        //3. ??????JWT
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();   //???????????????
        // ?????????????????????????????????Claims?????????get????????????????????????????????????????????????????????????
        Jws<Claims> jws = jwtParser.parseClaimsJws(pendingJWT);
        return jws.getBody().getSubject();
    }
}
