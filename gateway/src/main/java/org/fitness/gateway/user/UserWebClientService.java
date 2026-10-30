package org.fitness.gateway.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserWebClientService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> userExistsOrNot(String userId){
        System.out.println("Received userId: " + userId);
        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId).accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class,e -> {
                        if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                            return Mono.error(new RuntimeException("User Not Found "+userId));
                        else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                            return Mono.error(new RuntimeException("Bad Request  "+userId));
                        return Mono.error(new RuntimeException("Invalid "+userId));
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return false;
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        System.out.println("Received Email Request: " + request.getEmail());
        try {
            return userServiceWebClient.post()
                    .uri("/api/users/save").accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .onErrorResume(WebClientResponseException.class,e -> {
                        if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                            return Mono.error(new RuntimeException("Bad Request  "+e.getMessage()));
                        return Mono.error(new RuntimeException("Invalid "+e.getMessage()));
                    });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
