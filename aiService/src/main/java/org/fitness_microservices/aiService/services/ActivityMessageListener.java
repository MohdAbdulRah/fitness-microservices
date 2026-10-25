package org.fitness_microservices.aiService.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fitness_microservices.aiService.models.Activity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    private final ActivityAIService activityAIService;

    @KafkaListener(
            topics = "${kafka.topic.name}",
            groupId = "activity-processor-group"
    )
    public void processActivity(Activity activity) {
        log.info("========== KAFKA MESSAGE RECEIVED ==========");
        log.info("Activity: {}", activity);
        log.info("User ID: {}", activity.getUserId());
        activityAIService.generateRecommendation(activity);
    }
}
