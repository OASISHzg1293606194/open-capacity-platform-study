package com.open.capacity.client.filter;

import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.open.capacity.client.utils.TokenUtil;
import com.open.capacity.common.auth.props.PermitUrlProperties;

import reactor.core.publisher.Mono;

/**
 * 程序名 : AccessFilter 
 * 建立日期: 2018-09-09 
 * 作者 : someday 
 * 模块 : 网关 
 * 描述 : oauth校验 备注 :
 * version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Component
@SuppressWarnings("all")
@EnableConfigurationProperties(PermitUrlProperties.class)
public class AccessFilter implements GlobalFilter, Ordered {

	// url匹配器
	private AntPathMatcher pathMatcher = new AntPathMatcher();

	@Autowired
	private TokenStore tokenStore;

	@Resource
	private PermitUrlProperties permitUrlProperties;

	@Value("${security.oauth2.token.store.type:}")
	private String tokenType;

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return -500;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// TODO Auto-generated method stub

		try {
			if (!"redis".equals(tokenType)) {
				return chain.filter(exchange);
			}
			String accessToken = TokenUtil.extractToken(exchange.getRequest());

			// 默认
			boolean flag = false;

			flag = Lists.newArrayList(permitUrlProperties.getIgnored()).stream().anyMatch((item) -> {
				try {
					return pathMatcher.match(item, exchange.getRequest().getPath().value());
				} catch (Exception e) {
					return false;
				}
			}

			);

			if (flag) {
				return chain.filter(exchange);
			} else {

				OAuth2Authentication oauth2Authentication = tokenStore.readAuthentication(accessToken);
				if (oauth2Authentication != null) {
					return chain.filter(exchange);
				} else {
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

					ServerHttpResponse response = exchange.getResponse();
					JSONObject message = new JSONObject();
					message.put("code", 401);
					message.put("msg", "未认证通过！");
					byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
					DataBuffer buffer = response.bufferFactory().wrap(bits);
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					// 指定编码，否则在浏览器中会中文乱码
					response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
					return response.writeWith(Mono.just(buffer));
				}

			}
		} catch (Exception e) {
			return chain.filter(exchange);
		}

	}

}
