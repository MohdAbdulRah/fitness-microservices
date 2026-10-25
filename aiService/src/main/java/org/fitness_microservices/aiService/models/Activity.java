package org.fitness_microservices.aiService.models;

import jakarta.persistence.*;

import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;


@Data
public class Activity {

    private String Id;

    private String userId;

    private ActivityType type;

    private Integer duration;
    private Integer caloriesBurned;

    private LocalDateTime startTime;

    private Map<String,Object> additionalMetrics;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

}
