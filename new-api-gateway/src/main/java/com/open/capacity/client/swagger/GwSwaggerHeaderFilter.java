package com.open.capacity.client.swagger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

@Component
@SuppressWarnings("all")
@EnableConfigurationProperties(SwaggerButlerProperties.class)
public class GwSwaggerHeaderFilter extends AbstractGatewayFilterFactory {
	@Autowired
	private SwaggerButlerProperties swaggerButlerProperties;
    private static final String HEADER_NAME = "X-Forwarded-Prefix";
	 
	    @Override
	    public GatewayFilter apply(Object config) {
	        return (exchange, chain) -> {
	            ServerHttpRequest request = exchange.getRequest();
	            String path = request.getURI().getPath();
	            if (!StringUtils.endsWithIgnoreCase(path, swaggerButlerProperties.getApiDocsPath())) {
	                return chain.filter(exchange);
	            }
	            String basePath = path.substring(0, path.lastIndexOf(swaggerButlerProperties.getApiDocsPath()));
	            ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, basePath).build();
	            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
	            return chain.filter(newExchange);
	        };
	    }
 
}