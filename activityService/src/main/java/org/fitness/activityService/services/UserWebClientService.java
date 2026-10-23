package org.fitness.activityService.services;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserWebClientService {
    private final WebClient userServiceWebClient;

    public Boolean userExistsOrNot(String userId){
        System.out.println("Received userId: " + userId);
        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId).accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return false;
    }
}
