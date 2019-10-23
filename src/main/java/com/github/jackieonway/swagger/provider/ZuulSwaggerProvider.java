package com.github.jackieonway.swagger.provider;

import com.github.jackieonway.swagger.entity.SwaggerGatewayProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Profile;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.jackieonway.swagger.utils.SwaggerUtil.swaggerResource;

/**
 * @author Jackie
 */
@Profile("default")
public class ZuulSwaggerProvider implements SwaggerResourcesProvider {

    private static final String API_URI = "/v2/api-docs";
    //RouteLocator可以根据zuul配置的路由列表获取服务
    private final RouteLocator routeLocator;

    private final ZuulProperties zuulProperties;

    private final SwaggerGatewayProperties swaggerGatewayProperties;

    public ZuulSwaggerProvider(RouteLocator routeLocator
            , ZuulProperties zuulProperties
            , SwaggerGatewayProperties swaggerGatewayProperties) {
        this.routeLocator = routeLocator;
        this.zuulProperties = zuulProperties;
        this.swaggerGatewayProperties = swaggerGatewayProperties;
    }
 
    //这个方法用来添加swagger的数据源
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        //取出gateway的route
        routeLocator.getRoutes().forEach(route -> routes.add(route.getId()));
        Map<String, ZuulProperties.ZuulRoute> zuulRouteMap = zuulProperties.getRoutes();
        zuulRouteMap.entrySet().stream()
                .filter(zuulRoute -> routes.contains(zuulRoute.getValue().getId()))
                .forEach(zuulRoute -> resources.add(swaggerResource(zuulRoute.getKey(),
                        zuulRoute.getValue().getPath().replace("/**", API_URI),
                        swaggerGatewayProperties.getVersion())));
        return resources;
    }
}