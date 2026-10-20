package org.fitness.userservice.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.fitness.userservice.models.UserRole;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String userRole;

}
