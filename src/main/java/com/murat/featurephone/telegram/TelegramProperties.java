package com.murat.featurephone.telegram;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        int apiId,
        String apiHash
) {
}
