package com.project.bangjjack.domain.post.external.aimatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableConfigurationProperties(AiMatchApiProperties.class)
public class AiMatchClientConfig {

    @Bean(name = "aiMatchTaskExecutor", destroyMethod = "shutdown")
    public Executor aiMatchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ai-match-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();
        return executor;
    }

    @Bean
    public RestClient aiMatchRestClient(AiMatchApiProperties properties) {
        return buildClient(properties.baseUrl(), properties.connectTimeout(), properties.readTimeout());
    }

    @Bean
    public RestClient aiMatchBatchRestClient(AiMatchApiProperties properties) {
        return buildClient(properties.baseUrl(), properties.connectTimeout(), properties.readTimeoutBatch());
    }

    private RestClient buildClient(String baseUrl, int connectTimeoutMs, int readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new BufferingClientHttpRequestFactory(requestFactory))
                .requestInterceptor(loggingInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.info("AI match API request: {} {} (body {} bytes)",
                    request.getMethod(), request.getURI(), body.length);
            return execution.execute(request, body);
        };
    }
}
