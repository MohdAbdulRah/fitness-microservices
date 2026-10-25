package org.fitness_microservices.aiService.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.clone()
                .baseUrl("http://USERSERVICE")
                .build();
    } @Bean
    public WebClient activityServiceWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.clone()
                .baseUrl("http://ACTIVITYSERVICE")
                .build();
    }
}
