package com.murat.featurephone;

import com.murat.featurephone.telegram.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TelegramProperties.class)
public class FeaturePhoneGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeaturePhoneGatewayApplication.class, args);
    }

}
