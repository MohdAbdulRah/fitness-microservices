package org.fitness_microservices.aiService.controllers;

import lombok.RequiredArgsConstructor;
import org.fitness_microservices.aiService.models.Activity;
import org.fitness_microservices.aiService.models.Recommendation;
import org.fitness_microservices.aiService.requests.RecommendationRequest;
import org.fitness_microservices.aiService.responses.RecommendationResponse;
import org.fitness_microservices.aiService.responses.ResponseGlobal;
import org.fitness_microservices.aiService.services.ActivityAIService;
import org.fitness_microservices.aiService.services.GeminiService;
import org.fitness_microservices.aiService.services.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ActivityAIService activityAIService;

    @GetMapping("generateRecommendation/{activityId}")
    public RecommendationResponse generateRecommendation(@PathVariable String activityId){
        Activity activity = recommendationService.getActivity(activityId);
        Recommendation recom =  activityAIService.generateRecommendation(activity);

        return RecommendationResponse.builder()
                .id(recom.getId())
                .userId(recom.getUserId())
                .activityId(recom.getActivityId())
                .recommendation(recom.getRecommendation())
                .improvements(recom.getImprovements())
                .safety(recom.getSafety())
                .createdAt(recom.getCreatedAt())
                .updatedAt(recom.getUpdatedAt())
                .build();
    }

    @PostMapping("save")
    public ResponseEntity<ResponseGlobal> save(@RequestBody RecommendationRequest request){
        ResponseGlobal response = new ResponseGlobal();
        try{

            RecommendationResponse rec = recommendationService.save(request);
            response.setData(rec);
            response.setMessage("Sucessfully Saved Recommendation");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("getUserRecommendation/{userId}")
    public ResponseEntity<ResponseGlobal> getUserRecommendation(@PathVariable String userId){
        ResponseGlobal response = new ResponseGlobal();
        try{
            List<RecommendationResponse> recommend = recommendationService.getUserRecom(userId);
            response.setMessage("Fetched Users Recommendation Succesfully");
            response.setData(recommend);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch(Exception e){
            response.setData(null);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("getActivityRecommendation/{activityId}")
    public ResponseEntity<ResponseGlobal> getActivityRecommendation(@PathVariable String activityId){
        ResponseGlobal response = new ResponseGlobal();
        try{
            List<RecommendationResponse> recommed = recommendationService.getActivityRecommend(activityId);
            response.setMessage("Successfully Fetched the Recommendations");
            response.setData(recommed);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }
}
