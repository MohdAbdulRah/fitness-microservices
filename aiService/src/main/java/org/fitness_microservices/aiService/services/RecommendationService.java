package org.fitness_microservices.aiService.services;

import lombok.RequiredArgsConstructor;
import org.fitness_microservices.aiService.models.Activity;
import org.fitness_microservices.aiService.models.Recommendation;
import org.fitness_microservices.aiService.repositries.RecommendationRepository;
import org.fitness_microservices.aiService.requests.RecommendationRequest;
import org.fitness_microservices.aiService.responses.RecommendationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserWebClientService userServiceWebClient;

    public RecommendationResponse save(RecommendationRequest request){
        if(!userServiceWebClient.userExistsOrNot(request.getUserId())){
            throw new RuntimeException("User With this UserId Doesnt Exists");
        }if(!userServiceWebClient.activityExistsOrNot(request.getActivityId())){
            throw new RuntimeException("Activity With this ActivityId Doesnt Exists");
        }

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

    public List<RecommendationResponse> getUserRecom(String userId){
        List<Recommendation> list = recommendationRepository.findAllByUserId(userId);
        List<RecommendationResponse> recommendationResponses = list.stream()
                .map(recommendation -> RecommendationResponse
                        .builder()
                        .id(recommendation.getId())
                        .userId(recommendation.getUserId())
                        .activityId(recommendation.getActivityId())
                        .recommendation(recommendation.getRecommendation())
                        .improvements(recommendation.getImprovements())
                        .safety(recommendation.getSafety())
                        .suggestions(recommendation.getSuggestions())
                        .createdAt(recommendation.getCreatedAt())
                        .updatedAt(recommendation.getUpdatedAt())
                        .build()
                )
                .collect(Collectors.toList());
        return recommendationResponses;
    }

    public List<RecommendationResponse> getActivityRecommend(String activityId) {
        List<Recommendation> list = recommendationRepository.findAllByActivityId(activityId);

        List<RecommendationResponse> responnse = list
                .stream()
                .map(recommendation -> RecommendationResponse.builder()
                        .id(recommendation.getId())
                        .userId(recommendation.getUserId())
                        .activityId(recommendation.getActivityId())
                        .recommendation(recommendation.getRecommendation())
                        .improvements(recommendation.getImprovements())
                        .safety(recommendation.getSafety())
                        .suggestions(recommendation.getSuggestions())
                        .createdAt(recommendation.getCreatedAt())
                        .updatedAt(recommendation.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return  responnse;

    }

    public Activity getActivity(String activityId) {
        Activity activity = userServiceWebClient.activityGetOrNot(activityId);
        return activity;
    }
}
