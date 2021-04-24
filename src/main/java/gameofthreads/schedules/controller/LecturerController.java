package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AddLecturerRequest;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.service.LecturerService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/lecturers")
public class LecturerController {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);
    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(lecturerService.getAll());
    }

    @PostMapping()
    public ResponseEntity<?> add(@RequestBody AddLecturerRequest lecturerRequest) {
        return lecturerService.add(lecturerRequest)
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, success -> ResponseEntity.status(HttpStatus.CREATED).body(success));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return lecturerService.delete(id)
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, ResponseEntity::ok);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AddLecturerRequest addLecturerRequest) {
        Try<Either<Object, LecturerEntity>> result = Try.of(() -> lecturerService.update(id, addLecturerRequest));

        if (result.isFailure()) {
            LOGGER.error(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson());
            return ResponseEntity.badRequest().body(ErrorMessage.NOT_AVAILABLE_EMAIL.asJson());
        }

        return lecturerService.update(id, addLecturerRequest)
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, ResponseEntity::ok);
    }

}
