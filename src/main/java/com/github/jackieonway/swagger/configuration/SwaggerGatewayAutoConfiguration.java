/**
 * Jackie.
 * Copyright (c)) 2019 - 2019 All Right Reserved
 */
package com.github.jackieonway.swagger.configuration;

import com.github.jackieonway.swagger.entity.SwaggerGatewayProperties;
import com.github.jackieonway.swagger.filter.SwaggerHeaderFilter;
import com.github.jackieonway.swagger.provider.GatewaySwaggerProvider;
import com.github.jackieonway.swagger.provider.ZuulSwaggerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @author Jackie
 * @version $id: SwaggerGatewayAutoConfiguration.java v 0.1 2019-10-23 14:32 Jackie Exp $$
 */
@Configuration
@EnableConfigurationProperties(SwaggerGatewayProperties.class)
public class SwaggerGatewayAutoConfiguration {


    @EnableSwagger2
    @Configuration
    @ConditionalOnClass({ZuulProperties.class})
    public static class ZuulSwaggerConfiguration {

        @Bean
        @Primary
        public ZuulSwaggerProvider zuulSwaggerProvider(SwaggerGatewayProperties swaggerGatewayProperties,
               org.springframework.cloud.netflix.zuul.filters.RouteLocator  routeLocator,ZuulProperties zuulProperties)
                throws NoSuchFieldException, IllegalAccessException {
            ZuulSwaggerProvider zuulSwaggerProvider =  new ZuulSwaggerProvider(
                    routeLocator,zuulProperties,swaggerGatewayProperties);
            List<String> profiles = swaggerGatewayProperties.getProfiles();
            if (!CollectionUtils.isEmpty(profiles)){
                Profile annotation = zuulSwaggerProvider.getClass().getAnnotation(Profile.class);
                //获取 annotation 这个代理实例所持有的 InvocationHandler
                InvocationHandler h = Proxy.getInvocationHandler(annotation);
                // 获取 AnnotationInvocationHandler 的 memberValues 字段
                Field hField = h.getClass().getDeclaredField("memberValues");
                // 因为这个字段事 private final 修饰，所以要打开权限
                hField.setAccessible(true);
                // 获取 memberValues
                Map memberValues = (Map) hField.get(h);
                // 修改 value 属性值
                memberValues.put("value", profiles.toArray());
            }
            return zuulSwaggerProvider;
        }
    }

    @Configuration
    @ConditionalOnClass({GatewayProperties.class})
    public static class GatewaySwaggerConfiguration {
        @Bean
        public GatewaySwaggerProvider gatewaySwaggerProvider(SwaggerGatewayProperties swaggerGatewayProperties,
              org.springframework.cloud.gateway.route.RouteLocator routeLocator,GatewayProperties gatewayProperties)
                throws NoSuchFieldException, IllegalAccessException {
            GatewaySwaggerProvider gatewaySwaggerProvider =  new GatewaySwaggerProvider(
                    routeLocator,gatewayProperties,swaggerGatewayProperties);

            List<String> profiles = swaggerGatewayProperties.getProfiles();
            if (!CollectionUtils.isEmpty(profiles)){
                Profile annotation = gatewaySwaggerProvider.getClass().getAnnotation(Profile.class);
                //获取 annotation 这个代理实例所持有的 InvocationHandler
                InvocationHandler h = Proxy.getInvocationHandler(annotation);
                // 获取 AnnotationInvocationHandler 的 memberValues 字段
                Field hField = h.getClass().getDeclaredField("memberValues");
                // 因为这个字段事 private final 修饰，所以要打开权限
                hField.setAccessible(true);
                // 获取 memberValues
                Map memberValues = (Map) hField.get(h);
                // 修改 value 属性值
                memberValues.put("value", profiles.toArray());
            }
            return gatewaySwaggerProvider;
        }

        @Bean
        public SwaggerHeaderFilter swaggerHeaderFilter(){
            return new SwaggerHeaderFilter();
        }
    }

}
