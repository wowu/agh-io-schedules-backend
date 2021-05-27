package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.NotificationResponseList;
import gameofthreads.schedules.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping()
    public ResponseEntity<NotificationResponseList> getGlobalNotifications(){
        return ResponseEntity.ok(notificationService.getGlobalNotifications());
    }

}
