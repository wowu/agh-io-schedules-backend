package gameofthreads.schedules.notification;

import gameofthreads.schedules.dto.request.MyNotificationsDto;
import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.NotificationEntity;
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.notification.model.Conference;
import gameofthreads.schedules.notification.model.Meeting;
import gameofthreads.schedules.notification.model.Notification;
import gameofthreads.schedules.notification.model.ScheduleDetails;
import gameofthreads.schedules.repository.ConferenceRepository;
import gameofthreads.schedules.repository.NotificationRepository;
import gameofthreads.schedules.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component
public class EmailGateway {
    private final EmailQueue emailQueue;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ConferenceRepository conferenceRepository;
    private final EmailSender emailSender;

    public EmailGateway(EmailQueue emailQueue, NotificationRepository notificationRepository,
                        UserRepository userRepository, ConferenceRepository conferenceRepository,
                        EmailSender emailSender) {

        this.emailQueue = emailQueue;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.emailSender = emailSender;
    }

    public void add(Integer scheduleId, String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail_Email(email);

        if (userEntity.isEmpty() || userEntity.get().isGlobalNotifications()) {
            addGlobalNotification(scheduleId, email);
        }

        addLocalNotification(scheduleId, email);
    }

    private void addGlobalNotification(Integer scheduleId, String email) {
        createNewNotification(scheduleId);

        List<NotificationEntity> globalNotifications = notificationRepository.findAll()
                .stream()
                .filter(NotificationEntity::isGlobal)
                .collect(toList());

        for (NotificationEntity global : globalNotifications) {
            var details = new ScheduleDetails(global.getUnit(), global.getValue(), true);
            emailQueue.updateDetails(scheduleId, email, details);
        }
    }

    private void addLocalNotification(Integer scheduleId, String email) {
        createNewNotification(scheduleId);

        Set<NotificationEntity> globalNotifications = notificationRepository.findByUser_Email_Email(email);

        for (NotificationEntity global : globalNotifications) {
            var details = new ScheduleDetails(global.getUnit(), global.getValue(), false);
            emailQueue.updateDetails(scheduleId, email, details);
        }
    }

    private void createNewNotification(Integer scheduleId) {
        if (emailQueue.update(scheduleId) == null) {
            List<Conference> conferences = new ArrayList<>();

            for (ConferenceEntity conferenceEntity : conferenceRepository.fetchWithScheduleAndMeetings(scheduleId)) {
                List<Meeting> meetingsPerConference = conferenceEntity.getMeetingEntities()
                        .stream()
                        .map(MeetingEntity::buildMeetingNotification)
                        .collect(toList());

                conferences.add(new Conference(meetingsPerConference));
            }

            emailQueue.add(scheduleId, new Notification(conferences));
        }
    }

    public void delete(Integer scheduleId, String email) {
        emailQueue.delete(scheduleId, email);
    }

    public synchronized void reInitEmailQueue() {
        emailQueue.clear();
        emailSender.initEmailQueue();
    }

}
