package org.fitness_microservices.aiService.requests;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

    private String activityId;

    private String userId;
    private String recommendation;

    private List<String> improvements;

    private List<String> suggestions;


    private List<String> safety;
}
