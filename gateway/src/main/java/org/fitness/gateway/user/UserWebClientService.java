package org.fitness.gateway.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserWebClientService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> userExistsOrNot(String userId) {

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.just(false));
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {

        log.info("Registering user with email: {}", request.getEmail());

        return userServiceWebClient.post()
                .uri("/api/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("No error response body")
                                .flatMap(errorBody -> {
                                    log.error("Registration failed. Status: {}",
                                            response.statusCode());
                                    log.error("Registration error body: {}", errorBody);

                                    return Mono.error(
                                            new RuntimeException(
                                                    "User registration failed: " + errorBody
                                            )
                                    );
                                })
                )
                .bodyToMono(UserResponse.class);
    }
}
