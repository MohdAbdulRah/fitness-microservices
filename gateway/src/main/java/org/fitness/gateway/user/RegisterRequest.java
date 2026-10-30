package org.fitness.gateway.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email Format")
    private String email;

    private String keyCloakId;

    @NotBlank(message = "Password cannot be blank")
    @Length(min = 6,message = "Minimum 6 charcters should be present")
    private String password;
    private String firstName;
    private String lastName;


    private String userRole;

}
