package gameofthreads.schedules.service;

import gameofthreads.schedules.dto.request.MyNotificationsDto;
import gameofthreads.schedules.dto.response.NotificationResponse;
import gameofthreads.schedules.dto.response.NotificationResponseList;
import gameofthreads.schedules.entity.NotificationEntity;
import gameofthreads.schedules.entity.TimeUnit;
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.NotificationRepository;
import gameofthreads.schedules.repository.UserRepository;
import io.vavr.control.Either;
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

    public NotificationResponseList getGlobalNotifications() {
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

        List<NotificationResponse> withoutDuplicates = removeDuplicates(notificationsList.notifications);

        List<NotificationEntity> notificationsToSave = withoutDuplicates
                .stream()
                .map(notification -> new NotificationEntity(TimeUnit.getType(notification.unit), notification.value, admin))
                .collect(Collectors.toList());

        //TODO : update zamiast kasowania i wstawiania
        notificationRepository.deleteAll(notificationRepository.findByUser_Email_Email(admin.getEmailEntity().getEmail()));
        notificationRepository.saveAll(notificationsToSave);

        return new NotificationResponseList(withoutDuplicates);
    }

    public Either<Object, MyNotificationsDto> getMyNotifications(JwtAuthenticationToken token) {
        String lecturerEmail = (String) token.getTokenAttributes().get("sub");

        List<NotificationResponse> notifications = notificationRepository.findByUser_Email_Email(lecturerEmail)
                .stream()
                .map(NotificationEntity::buildResponse)
                .sorted()
                .collect(Collectors.toList());

        return userRepository.findByEmail_Email(lecturerEmail)
                .map(UserEntity::isGlobalNotifications)
                .map(isGlobal -> Either.right(new MyNotificationsDto(isGlobal, notifications)))
                .orElseGet(() -> Either.left(ErrorMessage.NO_LECTURER_WITH_EMAIL.asJson()));
    }

    @Transactional
    public MyNotificationsDto addMyNotifications(JwtAuthenticationToken token, MyNotificationsDto notificationsList) {
        String lecturerEmail = (String) token.getTokenAttributes().get("sub");

        UserEntity lecturer = userRepository.findByEmail_Email(lecturerEmail).orElseThrow();
        lecturer.setGlobalNotifications(notificationsList.isGlobal());
        final UserEntity updatedLecturer = userRepository.save(lecturer);

        List<NotificationResponse> withoutDuplicates = removeDuplicates(notificationsList.notifications);

        List<NotificationEntity> notificationsToSave = withoutDuplicates
                .stream()
                .map(notification -> new NotificationEntity(TimeUnit.getType(notification.unit), notification.value, updatedLecturer))
                .collect(Collectors.toList());

        //TODO : update zamiast kasowania i wstawiania
        notificationRepository.deleteAll(notificationRepository.findByUser_Email_Email(lecturerEmail));
        notificationRepository.saveAll(notificationsToSave);

        return new MyNotificationsDto(notificationsList.isGlobal(), withoutDuplicates);
    }

    public List<NotificationResponse> removeDuplicates(List<NotificationResponse> notificationResponses) {
        return notificationResponses.stream()
                .distinct()
                .collect(Collectors.toList());
    }

}
