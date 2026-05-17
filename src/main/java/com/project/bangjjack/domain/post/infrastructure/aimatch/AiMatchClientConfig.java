package com.project.bangjjack.domain.post.infrastructure.aimatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Configuration
@EnableConfigurationProperties(AiMatchApiProperties.class)
public class AiMatchClientConfig {

    @Bean
    public RestClient aiMatchRestClient(AiMatchApiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(properties.connectTimeout()));
        requestFactory.setReadTimeout(Duration.ofMillis(properties.readTimeout()));

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(new BufferingClientHttpRequestFactory(requestFactory))
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.info("AI match API request: {} {} body={}",
                    request.getMethod(), request.getURI(), new String(body, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        };
    }
}
