package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AddUserRequest;
import gameofthreads.schedules.dto.response.UserResponse;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.service.UserService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id, Authentication token) {
        Pair<HttpStatus, ?> response = userService.get(id, (JwtAuthenticationToken) token);

        return ResponseEntity.status(response.getFirst()).body(response.getSecond());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> add(@ModelAttribute AddUserRequest userRequest) {
        Try<Either<Object, UserResponse>> duplicateKey = Try.of(() -> userService.add(userRequest));

        if (duplicateKey.isFailure()) {
            return ResponseEntity.badRequest().body(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson());
        }

        return duplicateKey.get()
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, success -> ResponseEntity.status(HttpStatus.CREATED).body(success));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return userService.delete(id)
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, success -> ResponseEntity.noContent().build());
    }

    @PutMapping(value = {"/{id}"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestParam(required = false) String password,
                                    @RequestParam(required = false) Boolean activeSubscription) {

        Try<Either<Object, UserResponse>> result = Try.of(() -> userService.update(id, password, activeSubscription));

        if (result.isFailure()) {
            LOGGER.error(ErrorMessage.WRONG_USER_ID.asJson());
            return ResponseEntity.badRequest().body(ErrorMessage.WRONG_USER_ID.asJson());
        }

        return result.get()
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, ResponseEntity::ok);
    }

}
