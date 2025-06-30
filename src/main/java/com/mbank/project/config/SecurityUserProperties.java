package com.mbank.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/*
 * Class to hold security config values
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security.user")
public class SecurityUserProperties {
    private String username;
    private String password;
    private String roles;
}
