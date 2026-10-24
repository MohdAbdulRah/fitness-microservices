package org.fitness_microservices.aiService.controllers;

import lombok.RequiredArgsConstructor;
import org.fitness_microservices.aiService.requests.RecommendationRequest;
import org.fitness_microservices.aiService.responses.RecommendationResponse;
import org.fitness_microservices.aiService.responses.ResponseGlobal;
import org.fitness_microservices.aiService.services.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

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
}
