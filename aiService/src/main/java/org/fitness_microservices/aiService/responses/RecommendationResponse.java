package org.fitness_microservices.aiService.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecommendationResponse {
    private String id;
    private String activityId;

    private String userId;
    private String recommendation;

    private List<String> improvements;

    private List<String> suggestions;


    private List<String> safety;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
