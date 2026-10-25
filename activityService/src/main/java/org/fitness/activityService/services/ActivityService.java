package org.fitness.activityService.services;

import lombok.RequiredArgsConstructor;

import org.fitness.activityService.Repository.ActivityRepository;
import org.fitness.activityService.models.Activity;
import org.fitness.activityService.models.ActivityType;
import org.fitness.activityService.requests.ActivityRequest;
import org.fitness.activityService.responses.ActivityResponse;
import org.fitness.userservice.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserWebClientService userWebClientService;
    private final KafkaTemplate<String,Activity> kafkaTemplate;


    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse saveActivity(ActivityRequest request){
       if(!userWebClientService.userExistsOrNot(request.getUserId())){
           throw new RuntimeException("User With This ID Doesnt Exists");
       }

        Activity activity = new Activity();
        activity.setUserId(request.getUserId());
        activity.setType(ActivityType.valueOf(request.getType().toString()));
        activity.setStartTime(request.getStartTime());
        activity.setDuration(request.getDuration());
        activity.setCaloriesBurned(request.getCaloriesBurned());
        activity.setAdditionalMetrics(request.getAdditionalMetrics());

        activityRepository.save(activity);

        kafkaTemplate.send(topicName, activity.getUserId(), activity)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Kafka publishing failed: " + ex.getMessage());
                    } else {
                        System.out.println(
                                "Kafka published successfully. Topic: "
                                        + result.getRecordMetadata().topic()
                                        + ", Partition: "
                                        + result.getRecordMetadata().partition()
                                        + ", Offset: "
                                        + result.getRecordMetadata().offset()
                        );
                    }
                });
        ActivityResponse activityResponse = ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
        .type(activity.getType().toString())
                .startTime(request.getStartTime())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .additionalMetrics(request.getAdditionalMetrics())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
        return activityResponse;
    }

    public Boolean existsOrNot(String activityId) {
        return activityRepository.existsById(activityId);
    }

    public ActivityResponse getActivity(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity Not Found"));

        ActivityResponse response = ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .additionalMetrics(activity.getAdditionalMetrics())
                .caloriesBurned(activity.getCaloriesBurned())
                .duration(activity.getDuration())
                .startTime(activity.getStartTime())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
        return response;
    }
}
