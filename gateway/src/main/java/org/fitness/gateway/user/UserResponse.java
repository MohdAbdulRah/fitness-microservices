package org.fitness.gateway.user;

import lombok.Data;

@Data
public class UserResponse {
    private String Id;
    private String keyCloakId;
    private String Email;
    private String FirstName;
    private String LastName;
    private String userRole;
}
