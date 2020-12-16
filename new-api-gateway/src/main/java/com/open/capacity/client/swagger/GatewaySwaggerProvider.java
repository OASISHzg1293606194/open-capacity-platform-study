package com.open.capacity.client.swagger;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;

/**
 * @Primary注解的实例优先于其他实例被注入
 */

@Component
@Primary
@AllArgsConstructor
@EnableConfigurationProperties(SwaggerButlerProperties.class)
public class GatewaySwaggerProvider implements SwaggerResourcesProvider {
    @Resource
    private RouteLocator routeLocator;

    @Resource
    private SwaggerButlerProperties swaggerButlerProperties;

    private final GatewayProperties gatewayProperties;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        Set<String> routes = new HashSet<>();
        //取出Spring Cloud Gateway中的route
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        //结合application.yml中的路由配置，只获取有效的route节点
        gatewayProperties.getRoutes().stream().filter(
                routeDefinition -> (
                        //自动生成
                        swaggerButlerProperties.getAutoGenerateFromScgRoutes() ?
                                //配置路由服务不忽略文档的自动生成
                                routes.contains(routeDefinition.getId()) && !swaggerButlerProperties.needIgnore(routeDefinition.getId())
                                : false

                )
        ).forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                .forEach(predicateDefinition -> resources.add(
                        swaggerResource(
                                routeDefinition.getId(),
                                predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0").replace("/**", swaggerButlerProperties.getApiDocsPath())
                        )
                        )
                )
        );
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }


}