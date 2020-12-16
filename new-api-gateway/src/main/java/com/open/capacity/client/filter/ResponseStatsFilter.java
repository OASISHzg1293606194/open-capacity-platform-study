package com.open.capacity.client.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.constant.TraceConstant;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 程序名 : ResponseStatsFilter 建立日期: 2018-09-09 
 *  作者 : someday 
 *  模块 : 网关 
 *  描述 : 应答traceId:
 * version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ResponseStatsFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String traceId = MDC.get(TraceConstant.LOG_B3_TRACEID);
		MDC.put(TraceConstant.LOG_TRACE_ID, traceId);
		ServerHttpRequest request = exchange.getRequest();
		// 这里可以修改ServerHttpRequest实例
		ServerHttpResponse response = exchange.getResponse();
		// 这里可以修改ServerHttpResponse实例
		response.getHeaders().add(TraceConstant.HTTP_HEADER_TRACE_ID, traceId);
		// 构建新的ServerWebExchange实例
		
		
		log.info("response url " + request.getURI().getPath() + ", traceId = " + traceId);
		ServerWebExchange newExchange = exchange.mutate().request(exchange.getRequest()).response(response).build();
 
		
		
		return chain.filter(newExchange);

	}

}
