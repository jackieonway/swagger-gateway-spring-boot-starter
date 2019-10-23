/**
 * Jackie.
 * Copyright (c)) 2019 - 2019 All Right Reserved
 */
package com.github.jackieonway.swagger.utils;

import springfox.documentation.swagger.web.SwaggerResource;

/**
 * @author Jackie
 * @version $id: SwaggerUtil.java v 0.1 2019-10-23 14:55 Jackie Exp $$
 */
public class SwaggerUtil {

    private SwaggerUtil(){}

    public static SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
