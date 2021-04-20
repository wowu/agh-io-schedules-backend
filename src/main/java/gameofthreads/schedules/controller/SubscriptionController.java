package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AddSubscriptionRequest;
import gameofthreads.schedules.service.SubscriptionService;
import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addByAdmin(@RequestBody AddSubscriptionRequest requestData) {
        Either<String, Boolean> result = subscriptionService.addByAdmin(requestData);

        return result.isRight() ?
                ResponseEntity.ok(true) :
                ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/addByLink")
    public ResponseEntity<?> addUsingLink(@RequestParam String publicLink, @RequestParam String email) {
        Either<String, Boolean> result = subscriptionService.addUsingLink(publicLink, email);

        return result.isRight() ?
                ResponseEntity.ok(true) :
                ResponseEntity.badRequest().body(result.getLeft());
    }

}
