package org.fitness.activityService.requests;

import lombok.Data;
import org.fitness.activityService.models.ActivityType;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequest {
    private String userId;
    private String type;

    private Integer duration;
    private Integer caloriesBurned;

    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;
}
