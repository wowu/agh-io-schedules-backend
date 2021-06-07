package gameofthreads.schedules.notification;

import gameofthreads.schedules.entity.*;
import gameofthreads.schedules.notification.model.Conference;
import gameofthreads.schedules.notification.model.Meeting;
import gameofthreads.schedules.notification.model.Notification;
import gameofthreads.schedules.notification.model.ScheduleDetails;
import gameofthreads.schedules.repository.ConferenceRepository;
import gameofthreads.schedules.repository.LecturerRepository;
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
    private final LecturerRepository lecturerRepository;

    public EmailGateway(EmailQueue emailQueue, NotificationRepository notificationRepository,
                        UserRepository userRepository, ConferenceRepository conferenceRepository,
                        EmailSender emailSender, LecturerRepository lecturerRepository) {

        this.emailQueue = emailQueue;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.emailSender = emailSender;
        this.lecturerRepository = lecturerRepository;
    }

    public void add(Integer scheduleId, String email) {
        String fullName = lecturerRepository.findByEmail_Email(email)
                .map(LecturerEntity::getFullName).orElse("");

        Optional<UserEntity> userEntity = userRepository.findByEmail_Email(email);

        if (userEntity.isEmpty()) {
            addGlobalNotification(scheduleId, email, fullName.equals(""), fullName);
        } else if (userEntity.get().isGlobalNotifications()) {
            addGlobalNotification(scheduleId, email, fullName.equals(""), fullName);
        } else {
            addLocalNotification(scheduleId, email, fullName);
        }
    }

    private void addGlobalNotification(Integer scheduleId, String email, boolean full, String fullName) {
        createNewNotification(scheduleId);

        List<NotificationEntity> globalNotifications = notificationRepository.findAll()
                .stream()
                .filter(NotificationEntity::isGlobal)
                .collect(toList());

        for (NotificationEntity global : globalNotifications) {
            var details = new ScheduleDetails(global.getUnit(), global.getValue(), full, fullName);
            emailQueue.updateDetails(scheduleId, email, details);
        }
    }

    private void addLocalNotification(Integer scheduleId, String email, String fullName) {
        createNewNotification(scheduleId);

        Set<NotificationEntity> globalNotifications = notificationRepository.findByUser_Email_Email(email);

        for (NotificationEntity global : globalNotifications) {
            var details = new ScheduleDetails(global.getUnit(), global.getValue(), false, fullName);
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
