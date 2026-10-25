package org.fitness_microservices.aiService.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {
    private final WebClient webClient;

    @Value("${gemini.apiUrl}")
    private String geminiApiUrl;

    @Value("${gemini.apikey}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public String getRecommendation(String details) {

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", details)
                                }
                        )
                }
        );

        try {
            String response = webClient.post()
                    .uri(geminiApiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Gemini response: " + response);
            return response;

        } catch (org.springframework.web.reactive.function.client
                         .WebClientResponseException ex) {

            System.err.println("HTTP status: " + ex.getStatusCode());
            System.err.println("Error response: "
                    + ex.getResponseBodyAsString());

            throw ex;
        }
    }

}
