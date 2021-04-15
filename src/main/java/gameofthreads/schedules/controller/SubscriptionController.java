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
    public ResponseEntity<?> addSubscriptions(@RequestBody AddSubscriptionRequest requestData){
        Either<String, Boolean> result = subscriptionService.addSubscriptions(requestData);

        return result.isRight()?
                ResponseEntity.ok(true):
                ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/add/{public_link}/{email}")
    public ResponseEntity<?> addSubscription(@PathVariable(name = "public_link") String publicLink,
                                             @PathVariable(name = "email") String email){

        Either<String, Boolean> result = subscriptionService.addSubscriptionUsingLink(publicLink, email);

        return result.isRight()?
                ResponseEntity.ok(true):
                ResponseEntity.badRequest().body(result.getLeft());
    }

}
