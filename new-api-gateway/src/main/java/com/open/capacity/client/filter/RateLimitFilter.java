package com.open.capacity.client.filter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSONObject;
import com.open.capacity.client.service.RateLimitService;
import com.open.capacity.client.utils.TokenUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 程序名 : RateLimitFilter 
 * 建立日期: 2018-09-09 
 * 作者 : someday 
 * 模块 : 网关
 * 描述 : 限流过滤 
 * 备注 :
 * version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RateLimitFilter implements GlobalFilter, Ordered {
   
	@Autowired
	private Map<String,RateLimitService> rateLimitServiceMap ;

    @Override
    public int getOrder() {
        return -500;
    }

    

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken =  TokenUtil.extractToken(exchange.getRequest());   
        
        String reqUrl = exchange.getRequest().getPath().value();
        
    	AtomicInteger isOpenRateLimit = new AtomicInteger(0);

    	//超额自增处理
    	rateLimitServiceMap.forEach((k,v) -> { if(!v.checkRateLimit(reqUrl, accessToken)){ isOpenRateLimit.incrementAndGet();}});
		 
        //超额限流
        if ( isOpenRateLimit.get() > 0) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            ServerHttpResponse response = exchange.getResponse();
            JSONObject message = new JSONObject();
            message.put("code", 429);
            message.put("msg", "TOO MANY REQUESTS!");
            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
                DataBufferUtils.release(buffer);
            });

        }


        return chain.filter(exchange);
    }


     
}
