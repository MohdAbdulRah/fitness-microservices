package org.fitness.activityService.controllers;


import lombok.RequiredArgsConstructor;
import org.fitness.activityService.requests.ActivityRequest;
import org.fitness.activityService.responses.ActivityResponse;
import org.fitness.activityService.responses.ResponseGlobal;
import org.fitness.activityService.services.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("save")
    public ResponseEntity<ResponseGlobal> save(@RequestBody ActivityRequest request){
        ResponseGlobal response = new ResponseGlobal();
        try {
            ActivityResponse activity = activityService.saveActivity(request);
            response.setMessage("Saved Successfully");
            response.setData(activity);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }
        catch(Exception e){
            response.setMessage(e.getMessage());
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{activityId}/validate")
    public ResponseEntity<Boolean> validate(@PathVariable String activityId){

        try {
            return new ResponseEntity<>(activityService.existsOrNot(activityId),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{activityId}/getById")
    public ResponseEntity<ActivityResponse> getById(@PathVariable String activityId){

        try {
            return new ResponseEntity<>(activityService.getActivity(activityId),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ActivityResponse(), HttpStatus.BAD_REQUEST);
        }
    }

}
