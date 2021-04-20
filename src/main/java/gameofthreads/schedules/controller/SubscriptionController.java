package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.GetSubscribers;
import gameofthreads.schedules.dto.response.AddSubscription;
import gameofthreads.schedules.service.SubscriptionService;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/schedules")
public class SubscriptionController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{id}/subscribers")
    public ResponseEntity<?> add(@PathVariable Integer id, @RequestParam String email){
        Either<Object, AddSubscription> result = subscriptionService.add(id, email);

        if(result.isLeft()){
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        return ResponseEntity.ok(result.get());
    }

    @GetMapping("/{id}/subscribers")
    public ResponseEntity<?> get(@PathVariable Integer id){
        Either<Object, GetSubscribers> result = subscriptionService.findAll(id);

        if(result.isLeft()){
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        return ResponseEntity.ok(result.get());
    }

    @DeleteMapping("/{id}/subscriptions/{sub_id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable(name = "sub_id") Integer subscriptionId){
        Either<Object, Boolean> result = subscriptionService.delete(subscriptionId);

        if(result.isLeft()){
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        return ResponseEntity.ok(result.get());
    }

}
