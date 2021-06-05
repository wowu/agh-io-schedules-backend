package gameofthreads.schedules.notification;

import gameofthreads.schedules.entity.*;
import gameofthreads.schedules.notification.model.Conference;
import gameofthreads.schedules.notification.model.Meeting;
import gameofthreads.schedules.notification.model.Notification;
import gameofthreads.schedules.notification.model.ScheduleDetails;
import gameofthreads.schedules.repository.ConferenceRepository;
import gameofthreads.schedules.repository.NotificationRepository;
import gameofthreads.schedules.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class EmailSender {
    private final EmailQueue emailQueue;
    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ConferenceRepository conferenceRepository;

    public EmailSender(EmailQueue notificationQueue, NotificationRepository notificationRepository,
                       SubscriptionRepository subscriptionRepository, ConferenceRepository conferenceRepository) {

        this.emailQueue = notificationQueue;
        this.notificationRepository = notificationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.conferenceRepository = conferenceRepository;
    }

    public void initEmailQueue() {
        List<NotificationEntity> notificationEntities = notificationRepository.findAll();

        Map<Integer, List<ConferenceEntity>> conferenceGroupedBySchedule = conferenceRepository
                .fetchWithScheduleAndMeetings()
                .stream()
                .collect(groupingBy(conference -> conference.getScheduleEntity().getId()));

        List<NotificationEntity> globalNotifications = notificationEntities
                .stream()
                .filter(NotificationEntity::isGlobal)
                .collect(toList());

        List<SubscriptionEntity> subscriptionEntities = subscriptionRepository
                .findAll()
                .stream()
                .filter(SubscriptionEntity::isActive)
                .collect(toList());

        for (SubscriptionEntity subscription : subscriptionEntities) {
            Boolean isNotificationsEnabled = subscription.getSchedule().getNotifications();

            if(isNotificationsEnabled != null && !isNotificationsEnabled){
                continue;
            }

            Integer scheduleId = subscription.getSchedule().getId();
            List<ConferenceEntity> conferencePerSchedule = conferenceGroupedBySchedule.get(scheduleId);

            if (emailQueue.update(scheduleId) == null) {
                emailQueue.add(scheduleId, new Notification(prepareConference(conferencePerSchedule)));
            }

            if (subscription.getUser() == null || subscription.isGlobal()) {
                addNotifications(globalNotifications, scheduleId, subscription.getEmail());
            } else {
                List<NotificationEntity> userNotifications = notificationEntities
                        .stream()
                        .filter(notification -> notification.checkUser(subscription.getUser().getId()))
                        .collect(toList());

                userNotifications = userNotifications.size() == 0 ? globalNotifications : userNotifications;
                addNotifications(userNotifications, scheduleId, subscription.getEmail());
            }
        }
    }

    private List<Conference> prepareConference(List<ConferenceEntity> conferencePerSchedule) {
        List<Conference> conferences = new ArrayList<>();

        for (ConferenceEntity conferenceEntity : conferencePerSchedule) {
            List<Meeting> meetingsPerConference = conferenceEntity.getMeetingEntities()
                    .stream()
                    .map(MeetingEntity::buildMeetingNotification)
                    .collect(toList());

            conferences.add(new Conference(meetingsPerConference));
        }

        return conferences;
    }

    private void addNotifications(List<NotificationEntity> notifications, Integer scheduleId, String email) {
        for (NotificationEntity global : notifications) {
            var details = new ScheduleDetails(global.getUnit(), global.getValue(), true);
            emailQueue.updateDetails(scheduleId, email, details);
        }
    }

}
