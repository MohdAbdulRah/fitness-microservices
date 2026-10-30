package org.fitness.gateway;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fitness.gateway.user.RegisterRequest;
import org.fitness.gateway.user.UserWebClientService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {
    private final UserWebClientService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest request = getUserDetails(token);
        if(userId == null){
            userId = request.getKeyCloakId();
        }

        if(userId != null && token != null){
            String finalUserId = userId;
            return userService.userExistsOrNot(userId)
                    .flatMap(exist -> {
                        if(!exist){
                            if(request != null){
                                return userService.registerUser(request)
                                        .then(Mono.empty());
                            }
                            else{
                                return Mono.empty();
                            }
                        }
                        else {
                            log.info("User Already exists Skipping Sync");
                            return Mono.empty();
                        }

                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = (ServerHttpRequest) exchange.getRequest().mutate()
                                .header("X-User-Id", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request((Consumer<org.springframework.http.server.reactive.ServerHttpRequest.Builder>) mutatedRequest).build());
                    }));
        }
        return null;
    }

    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer","").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet jwtClaims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(jwtClaims.getClaimAsString("email"));
            request.setKeyCloakId(jwtClaims.getClaimAsString("sub"));
            request.setPassword("dummy@123");
            request.setFirstName(jwtClaims.getClaimAsString("given_name"));
            request.setLastName(jwtClaims.getClaimAsString("family_name"));

            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
