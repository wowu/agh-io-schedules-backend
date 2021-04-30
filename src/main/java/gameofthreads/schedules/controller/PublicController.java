package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.AddSubscription;
import gameofthreads.schedules.service.ScheduleService;
import gameofthreads.schedules.service.SubscriptionService;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public/schedules")
public class PublicController {
    private final Logger LOGGER = LoggerFactory.getLogger(PublicController.class);
    private final ScheduleService scheduleService;
    private final SubscriptionService subscriptionService;

    public PublicController(ScheduleService scheduleService, SubscriptionService subscriptionService) {
        this.scheduleService = scheduleService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getSchedule(@PathVariable String uuid) {
        Pair<?, Boolean> schedule = scheduleService.getScheduleInJson(uuid);

        return schedule.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(schedule.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schedule.getFirst());
    }

    @PostMapping(value = "/{uuid}/subscribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subscribe(@PathVariable String uuid, @RequestParam String email) {
        Either<Object, AddSubscription> result = subscriptionService.add(uuid, email);

        if (result.isLeft()) {
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        return ResponseEntity.ok(result.get());
    }
}
