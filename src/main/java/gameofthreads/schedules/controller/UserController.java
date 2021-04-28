package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AddUserRequest;
import gameofthreads.schedules.dto.response.UserResponse;
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.service.UserService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping()
    public ResponseEntity<?> add(@ModelAttribute AddUserRequest userRequest) {
        return userService.add(userRequest)
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
                }, ResponseEntity::ok);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<?> update(@PathVariable Integer id, @ModelAttribute AddUserRequest userRequest) {
        Try<Either<Object, UserResponse>> result = Try.of(() -> userService.update(id, userRequest));

        if (result.isFailure()) {
            LOGGER.error(ErrorMessage.WRONG_USER_ID.asJson());
            return ResponseEntity.badRequest().body(ErrorMessage.WRONG_USER_ID.asJson());
        }

        return userService.update(id, userRequest)
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, ResponseEntity::ok);
    }

}
