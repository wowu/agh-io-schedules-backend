package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AuthRequest;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.security.jwt.JwtCreator;
import gameofthreads.schedules.service.TokenService;
import io.vavr.control.Try;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/token")
public class TokenController {
    private final TokenService tokenService;
    private final JwtCreator jwtCreator;

    public TokenController(TokenService tokenService, JwtCreator jwtCreator) {
        this.tokenService = tokenService;
        this.jwtCreator = jwtCreator;
    }

    @PostMapping("/create")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {
        Try<ResponseEntity<?>> responseEntity =  Try.of(() -> tokenService.authenticate(authRequest))
                .map(claims -> jwtCreator.createWithClaims(authRequest.username, claims))
                .map(ResponseEntity::ok);

        return responseEntity.isSuccess() ?
                responseEntity.get():
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.WRONG_CREDENTIALS.asJson());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(Authentication token) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) token;

        if(!jwtToken.getTokenAttributes().containsKey("refresh")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.WRONG_REFRESH_TOKEN.asJson());
        }

        Try<ResponseEntity<?>> responseEntity = Try.of(() -> tokenService.refresh(jwtToken.getName()))
                .map(claims -> jwtCreator.createWithClaims(jwtToken.getName(), claims))
                .map(ResponseEntity::ok);

        return responseEntity.isSuccess() ?
                responseEntity.get():
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.WRONG_TOKENS_SUBJECT.asJson());
    }

}
