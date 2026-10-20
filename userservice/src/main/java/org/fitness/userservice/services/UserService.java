package org.fitness.userservice.services;

import lombok.RequiredArgsConstructor;
import org.fitness.userservice.models.User;
import org.fitness.userservice.models.UserRole;
import org.fitness.userservice.repositories.repositories.UserRepository;
import org.fitness.userservice.requests.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User registerUser(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("User With Email Already Exists");
        }
        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        try {
            user.setRole(UserRole.valueOf(request.getUserRole().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Invalid user role: " + request.getUserRole());
        }

        User user1 = userRepository.save(user);
        return user1;
    }
}
