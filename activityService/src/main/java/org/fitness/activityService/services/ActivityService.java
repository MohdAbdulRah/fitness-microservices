package org.fitness.activityService.services;

import lombok.RequiredArgsConstructor;
import org.fitness.activityService.Repository.ActivityRepository;
import org.fitness.activityService.models.Activity;
import org.fitness.activityService.models.ActivityType;
import org.fitness.activityService.requests.ActivityRequest;
import org.fitness.activityService.responses.ActivityResponse;
import org.fitness.userservice.models.User;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityResponse saveActivity(ActivityRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() ->{
            throw new RuntimeException("UserId Doesnt exists");
        });

        Activity activity = new Activity();
        activity.setUserId(request.getUserId());
        activity.setType(ActivityType.valueOf(request.getType().toString()));
        activity.setStartTime(request.getStartTime());
        activity.setDuration(request.getDuration());
        activity.setCaloriesBurned(request.getCaloriesBurned());
        activity.setAdditionalMetrics(request.getAdditionalMetrics());

        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setUserId(user);
        activityResponse.setType(activity.getType().toString());
        activityResponse.setStartTime(request.getStartTime());
        activityResponse.setDuration(request.getDuration());
        activityResponse.setCaloriesBurned(request.getCaloriesBurned());
        activityResponse.setAdditionalMetrics(request.getAdditionalMetrics());

        return activityResponse;
    }
}
