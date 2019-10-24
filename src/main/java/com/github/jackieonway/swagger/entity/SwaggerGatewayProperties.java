/**
 * Jackie.
 * Copyright (c)) 2019 - 2019 All Right Reserved
 */
package com.github.jackieonway.swagger.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Jackie
 * @version $id: SwaggerGatewayProperties.java v 0.1 2019-10-23 14:32 Jackie Exp $$
 */
@ConfigurationProperties("spring.jackieonway.swagger.gateway")
public class SwaggerGatewayProperties {

    /**
     *  swagger profiles ; notice : this param is invalid
     */
    private List<String> profiles;

    /**
     *  swagger interface version default 1.0
     */
    private String version = "1.0";

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
