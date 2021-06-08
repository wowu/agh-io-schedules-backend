package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.NotificationResponseList;
import gameofthreads.schedules.notification.EmailGateway;
import gameofthreads.schedules.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final EmailGateway emailGateway;

    public NotificationController(NotificationService notificationService, EmailGateway emailGateway) {
        this.notificationService = notificationService;
        this.emailGateway = emailGateway;
    }

    @GetMapping()
    public ResponseEntity<NotificationResponseList> getGlobalNotifications(){
        return ResponseEntity.ok(notificationService.getGlobalNotifications());
    }

    @PutMapping()
    public ResponseEntity<NotificationResponseList> addGlobalNotifications(
            @RequestBody NotificationResponseList notifications){

        NotificationResponseList notificationResponseList = notificationService.addGlobalNotifications(notifications);
        CompletableFuture.runAsync(emailGateway::reInit);
        return ResponseEntity.ok(notificationResponseList);
    }

}
