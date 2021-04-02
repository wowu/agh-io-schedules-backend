package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AuthRequest;
import gameofthreads.schedules.security.jwt.JwtCreator;
import gameofthreads.schedules.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    private final JwtCreator jwtCreator;

    public UserController(UserService userService, JwtCreator jwtCreator) {
        this.userService = userService;
        this.jwtCreator = jwtCreator;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest authRequest){
        try{
            Map<String, String> claims = userService.authenticate(authRequest);
            String token = jwtCreator.createWithClaims(authRequest.username, claims);
            return ResponseEntity.ok(token);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username or password");
        }
    }

}
