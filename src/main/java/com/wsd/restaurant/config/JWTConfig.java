package com.wsd.restaurant.config;

import com.wsd.restaurant.util.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:configuration.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
@NoArgsConstructor
public class JWTConfig {
    private String jwtSecret;
    private int jwtExpiration;
}
