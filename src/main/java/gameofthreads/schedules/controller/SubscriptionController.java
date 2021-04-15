package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.request.AddSubscriptionRequest;
import gameofthreads.schedules.service.SubscriptionService;
import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/subscription/")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<?> addSubscriptions(AddSubscriptionRequest requestData){
        Either<String, Boolean> result = subscriptionService.addSubscription(requestData);

        return result.isRight()?
                ResponseEntity.ok(true):
                ResponseEntity.badRequest().body(result.getLeft());
    }

}
