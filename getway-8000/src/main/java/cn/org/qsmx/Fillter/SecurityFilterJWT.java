package cn.org.qsmx.Fillter;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
@Component
@Slf4j
public class SecurityFilterJWT extends BaseInfoProperties implements GlobalFilter, Ordered {

    public static final String HEADER_USER_TOKEN = "headerUserToken";
    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;
    @Autowired
    private JWTUtil jwtUtil;
    //路劲匹配器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获取当前请求路径
        String url = exchange.getRequest().getURI().getPath();
        log.info("url={}",url);
        //获取所有需要排除校验的URL
        List<String> excludeList = excludeUrlProperties.getUrls();
        //校验并排除exexcludeList
        if(excludeList!=null&& !excludeList.isEmpty()){
            for (String exclude : excludeList){
                if(antPathMatcher.match(exclude,url)){
                    //放行
                    return chain.filter(exchange);
                }
            }
        }
        log.info("被拦截了");

        //判断header是否有token，对用户请求进行判断拦截
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userToken = headers.getFirst(HEADER_USER_TOKEN);

        //判断header中的令牌
        if(StringUtils.isNotBlank(userToken)){
            String[] tokenArr = userToken.split(JWTUtil.at);
            if(tokenArr.length<2){
                return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);
            }

            //获得jwt令牌与前缀
            String prefix = tokenArr[0];
            String jwt = tokenArr[1];

            //判断并且处理用户信息
            if (prefix.equalsIgnoreCase(TOKEN_USER_PREFIX)){
                //校验JWT
                return dealJWT(jwt,exchange,chain,APP_USER_JSON);
            }else if (prefix.equalsIgnoreCase(TOKEN_ADMIN_PREFIX)){
                //校验JWT
                return dealJWT(jwt,exchange,chain,ADMIN_USER_JSON);
            }else if(prefix.equalsIgnoreCase(TOKEN_SAAS_PREFIX)){
                //校验JWT
                return dealJWT(jwt,exchange,chain,SAAS_USER_JSON);
            }


        }
        //不放行
        //GraceException.display(ResponseStatusEnum.UN_LOGIN);
        return renderErrorMsg(exchange,ResponseStatusEnum.UN_LOGIN);

    }

    /**
     * JWT校验
     * @param jwt
     * @param exchange
     * @param chain
     * @param key
     * @return
     */
    public Mono<Void> dealJWT(String jwt,
                              ServerWebExchange exchange,
                              GatewayFilterChain chain,
                              String key){
        try {
            String userJson = jwtUtil.checkJWT(jwt);
            ServerWebExchange serverExchange = setNewHeader(exchange,key,userJson);
            return chain.filter(serverExchange);
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            return renderErrorMsg(exchange,ResponseStatusEnum.JWT_EXPIRE_ERROR);
        } catch (Exception e){
            e.printStackTrace();
            return renderErrorMsg(exchange,ResponseStatusEnum.JWT_SIGNATURE_ERROR);
        }

    }

    /**
     * 传递用户信息
     * @param exchange
     * @param headerKey
     * @param headerValue
     * @return
     */
    public ServerWebExchange setNewHeader(ServerWebExchange exchange,
                                          String headerKey,
                                          String headerValue){
        //重新构建新的request
        ServerHttpRequest request = exchange.getRequest()
                                            .mutate()
                                            .header(headerKey, headerValue)
                                            .build();
        //替换原来的request
        return exchange.mutate().request(request).build();
    }

    /**
     * 重新包装且返回错误信息
     * @param exchange 原ServerWebExchange
     * @param responseStatusEnum
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange,ResponseStatusEnum responseStatusEnum){

        //1,获得responce
        ServerHttpResponse response = exchange.getResponse();

        //2. 构件jsonresult
        GraceJSONResult jsonResult = GraceJSONResult.exception(responseStatusEnum);
        //3. 修改response 的code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        //4. 设定header类型
        if (!response.getHeaders().containsKey("Content-Type")){
            response.getHeaders()
                    .add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        //5. 转换json并且向response中写入数据
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return  response.writeWith(Mono.just(dataBuffer));
    }


    //过滤器的顺序，数字越小优先级越大
    @Override
    public int getOrder() {
        return 0;
    }
}
