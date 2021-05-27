package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.response.NotificationResponse;
import gameofthreads.schedules.dto.response.NotificationResponseList;
import gameofthreads.schedules.entity.NotificationEntity;
import gameofthreads.schedules.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponseList getGlobalNotifications(){
        List<NotificationResponse> notifications = notificationRepository.findAll()
                .stream()
                .filter(NotificationEntity::isGlobal)
                .map(NotificationEntity::buildResponse)
                .collect(Collectors.toList());

        return new NotificationResponseList(notifications);
    }
}
