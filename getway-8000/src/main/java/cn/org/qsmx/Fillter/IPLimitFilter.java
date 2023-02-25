package cn.org.qsmx.Fillter;

import cn.org.qsmx.base.BaseInfoProperties;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.util.IPUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
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
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Autowired
    private ExcludeUrlProperties excludeUrlProperties;

    //路劲匹配器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${blackIP.continueCounts}")
    private Integer continueCounts;
    @Value("${blackIP.timeInterval}")
    private Integer timeInterval;
    @Value("${blackIP.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取当前请求路径
        String url = exchange.getRequest().getURI().getPath();

        //获取所有需要进行限流校验的url
        List<String> ipLimitList = excludeUrlProperties.getIpLimitUrls();

        //校验
        if (ipLimitList != null && !ipLimitList.isEmpty()) {
            for (String limitUrl : ipLimitList) {
                if (antPathMatcher.match(limitUrl, url)) {
                    //如果匹配到，则表明需要进行ip的拦截校验
                    log.info("测试" + url);
                    return doLimit(exchange,chain);
                }
            }
        }

        //默认放行
        return chain.filter(exchange);

    }

    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        //根据request获取ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);
        /*
          需求：
          判断ip在20s内请求次数是否超过3次
          如果超过，则限制访问30s
          等待30s静默以后，才能够恢复请求
         */
        //正常ip
        final String ipRedisKey = "gateway-ip:" + ip;
        //被拦截的ip
        final String ipRedisLimitedKey = "gateway-ip:limit:" + ip;
        //获取当前ip，查询还剩下多少时间
        long limitLeftTimes = redis.ttl(ipRedisLimitedKey);
        if (limitLeftTimes > 0) {
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        //在redis中获得ip累加次数
        long requestCounts = redis.increment(ipRedisKey, 1);
        //判断第一次
        if (requestCounts == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }

        if (requestCounts > continueCounts) {
            redis.set(ipRedisLimitedKey, ipRedisLimitedKey, limitTimes);
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        return chain.filter(exchange);
    }

    /**
     * 重新包装且返回错误信息
     *
     * @param exchange           原ServerWebExchange
     * @param responseStatusEnum
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum responseStatusEnum) {

        //1,获得responce
        ServerHttpResponse response = exchange.getResponse();

        //2. 构件jsonresult
        GraceJSONResult jsonResult = GraceJSONResult.exception(responseStatusEnum);
        //3. 修改response 的code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        //4. 设定header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders()
                    .add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        //5. 转换json并且向response中写入数据
        String resultJson = new Gson().toJson(jsonResult);
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(resultJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }


    //过滤器的顺序，数字越小优先级越大
    @Override
    public int getOrder() {
        return 1;
    }
}
