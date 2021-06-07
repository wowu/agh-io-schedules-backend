package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.AddSubscription;
import gameofthreads.schedules.dto.response.GetSubscribers;
import gameofthreads.schedules.notification.EmailGateway;
import gameofthreads.schedules.service.SubscriptionService;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/schedules")
public class SubscriptionController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);
    private final SubscriptionService subscriptionService;
    private final EmailGateway emailGateway;

    public SubscriptionController(SubscriptionService subscriptionService, EmailGateway emailGateway) {
        this.subscriptionService = subscriptionService;
        this.emailGateway = emailGateway;
    }

    @PostMapping(value = "/{id}/subscribers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> add(@PathVariable Integer id, @RequestParam String email) {
        Either<Object, AddSubscription> result = subscriptionService.add(id, email);

        if (result.isLeft()) {
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        CompletableFuture.runAsync(() -> emailGateway.add(id, email));
        return ResponseEntity.ok(result.get());
    }

    @GetMapping("/{id}/subscribers")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        Either<Object, GetSubscribers> result = subscriptionService.findAll(id);

        if (result.isLeft()) {
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        return ResponseEntity.ok(result.get());
    }

    @DeleteMapping("/{id}/subscribers/{sub_id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable(name = "sub_id") Integer subscriptionId) {
        Either<Object, Boolean> result = subscriptionService.delete(subscriptionId);

        if (result.isLeft()) {
            LOGGER.info(result.getLeft().toString());
            return ResponseEntity.badRequest().body(result.getLeft());
        }

        return ResponseEntity.noContent().build();
    }

}
