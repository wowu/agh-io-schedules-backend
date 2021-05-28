package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.NotificationResponseList;
import gameofthreads.schedules.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping()
    public ResponseEntity<NotificationResponseList> getGlobalNotifications(){
        return ResponseEntity.ok(notificationService.getGlobalNotifications());
    }

    @PutMapping()
    public ResponseEntity<NotificationResponseList> addGlobalNotifications(
            @RequestBody NotificationResponseList notifications){

        return ResponseEntity.ok(notificationService.addGlobalNotifications(notifications));
    }

}
