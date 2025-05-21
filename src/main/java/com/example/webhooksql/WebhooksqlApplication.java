package com.example.webhooksql;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebhooksqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhooksqlApplication.class, args);
    }

    @Bean
    CommandLineRunner run(WebhookService webhookService) {
        return args -> {
            webhookService.processWebhookFlow();
        };
    }
}
