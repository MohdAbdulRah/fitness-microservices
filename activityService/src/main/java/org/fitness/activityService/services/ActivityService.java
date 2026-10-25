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
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setType(activity.getType().toString());
        activityResponse.setStartTime(request.getStartTime());
        activityResponse.setDuration(request.getDuration());
        activityResponse.setCaloriesBurned(request.getCaloriesBurned());
        activityResponse.setAdditionalMetrics(request.getAdditionalMetrics());
        activityResponse.setCreatedAt(activity.getCreatedAt());
        activityResponse.setUpdatedAt(activity.getUpdatedAt());
        return activityResponse;
    }

    public Boolean existsOrNot(String activityId) {
        return activityRepository.existsById(activityId);
    }
}
