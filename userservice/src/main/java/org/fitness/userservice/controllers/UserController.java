package org.fitness.userservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.fitness.userservice.models.User;
import org.fitness.userservice.requests.RegisterRequest;
import org.fitness.userservice.responses.ResponseGlobal;
import org.fitness.userservice.responses.UserResponse;
import org.fitness.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<ResponseGlobal> getUserById(@PathVariable String userId){
        ResponseGlobal response = new ResponseGlobal();

        try
        {
             UserResponse user = userService.getUser(userId);
             response.setMessage("User Fetched Successfully");
             response.setData(user);

             return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch(Exception e){
            response.setMessage(e.getMessage());
            response.setData(null);

            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




    @PostMapping("save")
    public ResponseEntity<ResponseGlobal> save(@Valid @RequestBody RegisterRequest request){
        ResponseGlobal response = new ResponseGlobal();
        try{
               User user = userService.registerUser(request);
               UserResponse userResponse = new UserResponse();
               userResponse.setId(user.getId());
               userResponse.setEmail(user.getEmail());
               userResponse.setFirstName(user.getFirstName());
               userResponse.setLastName(user.getLastName());
               userResponse.setUserRole(user.getRole().toString());

               response.setMessage("Saved Sucessfully");
               response.setData(userResponse);
               return new ResponseEntity<>(response,HttpStatus.OK);
          }
          catch (Exception e){
              response.setMessage(e.getMessage());
              response.setData(null);
              return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }
}
