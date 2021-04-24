package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AddLecturerRequest;
import gameofthreads.schedules.service.LecturerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/lecturers")
public class LecturerController {
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
                .fold(error -> ResponseEntity.badRequest().body(error),
                        success -> ResponseEntity.status(HttpStatus.CREATED).body(success));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return lecturerService.delete(id)
                .fold(error -> ResponseEntity.badRequest().body(error), ResponseEntity::ok);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AddLecturerRequest addLecturerRequest){
        return lecturerService.update(id, addLecturerRequest)
                .fold(error -> ResponseEntity.badRequest().body(error), ResponseEntity::ok);
    }

}
