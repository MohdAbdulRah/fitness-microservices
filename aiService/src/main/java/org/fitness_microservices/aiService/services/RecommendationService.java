package org.fitness_microservices.aiService.services;

import lombok.RequiredArgsConstructor;
import org.fitness_microservices.aiService.models.Recommendation;
import org.fitness_microservices.aiService.repositries.RecommendationRepository;
import org.fitness_microservices.aiService.requests.RecommendationRequest;
import org.fitness_microservices.aiService.responses.RecommendationResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public RecommendationResponse save(RecommendationRequest request){
        Recommendation recommendation = Recommendation.builder()
                .activityId(request.getActivityId())
                .userId(request.getUserId())
                .recommendation(request.getRecommendation())
                .improvements(request.getImprovements())
                .safety(request.getSafety())
                .suggestions(request.getSuggestions())
                .build();
        recommendation = recommendationRepository.save(recommendation);
        RecommendationResponse response = RecommendationResponse.builder()
                .id(recommendation.getId())
                .activityId(recommendation.getActivityId())
                .userId(recommendation.getUserId())
                .recommendation(recommendation.getRecommendation())
                .improvements(recommendation.getImprovements())
                .safety(recommendation.getSafety())
                .suggestions(recommendation.getSuggestions())
                .createdAt(recommendation.getCreatedAt())
                .updatedAt(recommendation.getUpdatedAt())
                .build();

        return response;
    }
}
