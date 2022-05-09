package com.github.mihaildemidoff.itpoker.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("telegram")
@Data
public class TelegramProperties {
    private String token;
    private String name;
}
