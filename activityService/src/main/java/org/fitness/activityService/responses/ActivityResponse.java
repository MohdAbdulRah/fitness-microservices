package org.fitness.activityService.responses;

import lombok.Data;
import org.fitness.userservice.models.User;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponse {
    private User UserId;
    private String type;

    private Integer duration;
    private Integer caloriesBurned;

    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
