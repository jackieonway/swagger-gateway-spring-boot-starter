package com.github.jackieonway.swagger.filter;

import com.github.jackieonway.swagger.provider.GatewaySwaggerProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Jackie
 */
public class SwaggerHeaderFilter extends AbstractGatewayFilterFactory {
    private static final String HEADER_NAME = "X-Forwarded-Prefix";
 
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            if (!StringUtils.endsWithIgnoreCase(path, GatewaySwaggerProvider.API_URI)) {
                return chain.filter(exchange);
            }
            String basePath = path.substring(0, path.lastIndexOf(GatewaySwaggerProvider.API_URI));
            String[] basePathArr = {basePath};
            ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, basePathArr).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        };
    }
}