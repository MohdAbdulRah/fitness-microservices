
package org.fitness.gateway;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fitness.gateway.user.RegisterRequest;
import org.fitness.gateway.user.UserWebClientService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserWebClientService userService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Don't synchronize users for Actuator requests
        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        // No Bearer token: continue normally.
        // Spring Security will handle authentication.
        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        RegisterRequest request;

        try {
            request = getUserDetails(token);
        } catch (Exception e) {
            log.error("Failed to extract Keycloak user details", e);
            return Mono.error(e);
        }

        String userId = request.getKeyCloakId();

        if (userId == null || userId.isBlank()) {
            return Mono.error(
                    new IllegalArgumentException("Keycloak subject is missing")
            );
        }

        log.info("Synchronizing Keycloak user: {}", userId);

        return userService.userExistsOrNot(userId)
                .flatMap(exists -> {

                    if (exists) {
                        log.info("User already exists: {}", userId);
                        return Mono.empty();
                    }

                    log.info("Registering new Keycloak user: {}", userId);

                    return userService.registerUser(request)
                            .then();
                })
                .then(Mono.defer(() -> {

                    ServerHttpRequest mutatedRequest =
                            exchange.getRequest()
                                    .mutate()
                                    .headers(headers ->
                                            headers.set("X-User-Id", userId))
                                    .build();

                    return chain.filter(
                            exchange.mutate()
                                    .request(mutatedRequest)
                                    .build()
                    );
                }))
                .doOnError(error ->
                        log.error("Keycloak synchronization failed", error));
    }

    private RegisterRequest getUserDetails(String token) throws Exception {

        String tokenWithoutBearer = token.substring(7).trim();

        SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        RegisterRequest request = new RegisterRequest();

        request.setEmail(claims.getClaimAsString("email"));
        request.setKeyCloakId(claims.getSubject());
        request.setFirstName(claims.getClaimAsString("given_name"));
        request.setLastName(claims.getClaimAsString("family_name"));
        request.setUserRole("USER");
        request.setPassword("dummy@123");
        return request;
    }
}
