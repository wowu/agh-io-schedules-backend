package gameofthreads.schedules.controller;

import gameofthreads.schedules.service.ScheduleService;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/public/schedules")
public class PublicController {
    private final ScheduleService scheduleService;

    public PublicController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getSchedule(@PathVariable String uuid) {
        Pair<?, Boolean> schedule = scheduleService.getScheduleInJson(uuid);

        return schedule.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(schedule.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schedule.getFirst());
    }
}
