package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.AddMyNotificationsRequest;
import gameofthreads.schedules.dto.response.NotificationResponse;
import gameofthreads.schedules.dto.response.NotificationResponseList;
import gameofthreads.schedules.entity.NotificationEntity;
import gameofthreads.schedules.entity.TimeUnit;
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.repository.NotificationRepository;
import gameofthreads.schedules.repository.UserRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationResponseList getGlobalNotifications(){
        List<NotificationResponse> notifications = notificationRepository.findAll()
                .stream()
                .filter(NotificationEntity::isGlobal)
                .map(NotificationEntity::buildResponse)
                .collect(Collectors.toList());

        return new NotificationResponseList(notifications);
    }

    @Transactional
    public NotificationResponseList addGlobalNotifications(NotificationResponseList notificationsList) {
        UserEntity admin = userRepository.findAdmin();

        List<NotificationEntity> notificationsToSave = notificationsList.notifications
                .stream()
                .map(notification -> new NotificationEntity(TimeUnit.getType(notification.unit), notification.value, admin))
                .collect(Collectors.toList());

        notificationRepository.saveAll(notificationsToSave);
        return notificationsList;
    }

    public NotificationResponseList getMyNotifications(JwtAuthenticationToken token){
        String lecturerEmail = (String) token.getTokenAttributes().get("sub");

        List<NotificationResponse> notifications = notificationRepository.findByUser_Email_Email(lecturerEmail)
                .stream()
                .map(NotificationEntity::buildResponse)
                .collect(Collectors.toList());

        return new NotificationResponseList(notifications);
    }

    @Transactional
    public AddMyNotificationsRequest addMyNotifications(JwtAuthenticationToken token, AddMyNotificationsRequest notificationsList){
        String lecturerEmail = (String) token.getTokenAttributes().get("sub");

        UserEntity lecturer = userRepository.findByEmail_Email(lecturerEmail).orElseThrow();
        lecturer.setGlobalNotifications(notificationsList.global);
        final UserEntity updatedLecturer = userRepository.save(lecturer);

        List<NotificationEntity> notificationsToSave = notificationsList.notifications
                .stream()
                .map(notification -> new NotificationEntity(TimeUnit.getType(notification.unit), notification.value, updatedLecturer))
                .collect(Collectors.toList());

        notificationRepository.saveAll(notificationsToSave);
        return notificationsList;
    }

}
