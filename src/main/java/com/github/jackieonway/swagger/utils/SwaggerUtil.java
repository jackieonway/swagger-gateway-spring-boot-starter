/**
 * Jackie.
 * Copyright (c)) 2019 - 2019 All Right Reserved
 */
package com.github.jackieonway.swagger.utils;

import com.github.jackieonway.swagger.entity.CloudRoute;
import org.springframework.util.CollectionUtils;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jackie
 * @version $id: SwaggerUtil.java v 0.1 2019-10-23 14:55 Jackie Exp $$
 */
public class SwaggerUtil {

    private SwaggerUtil(){}

    public static SwaggerResource swaggerResource(String name, String location, String version, List<CloudRoute> routes) {
        SwaggerResource swaggerResource = new SwaggerResource();
        if (CollectionUtils.isEmpty(routes)){
            swaggerResource.setName(name);
        }else {
            routes.forEach(route -> {
                if (Objects.equals(route.getId(), name)){
                    swaggerResource.setName(route.getName());
                }
            });
        }

        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
