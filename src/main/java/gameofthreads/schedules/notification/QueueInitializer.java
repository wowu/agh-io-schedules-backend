package gameofthreads.schedules.notification;

import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.NotificationEntity;
import gameofthreads.schedules.entity.SubscriptionEntity;
import gameofthreads.schedules.notification.model.Meeting;
import gameofthreads.schedules.repository.ConferenceRepository;
import gameofthreads.schedules.repository.NotificationRepository;
import gameofthreads.schedules.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class QueueInitializer {
    private final EmailQueue emailQueue;
    private final NotificationRepository notificationRepository;
    private final ConferenceRepository conferenceRepository;
    private final SubscriptionRepository subscriptionRepository;

    public QueueInitializer(EmailQueue emailQueue, NotificationRepository notificationRepository,
                            ConferenceRepository conferenceRepository, SubscriptionRepository subscriptionRepository) {

        this.emailQueue = emailQueue;
        this.notificationRepository = notificationRepository;
        this.conferenceRepository = conferenceRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    public void initialize() {
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

        conferenceGroupedBySchedule.forEach((k, v) -> {
            List<SubscriptionEntity> subscriptions = subscriptionEntities.stream()
                    .filter(e -> e.getSchedule().getId().equals(k))
                    .collect(toList());

            for (SubscriptionEntity subscription : subscriptions) {

                // When add by public link
                if (subscription.getLecturer() == null) {
                    v.forEach(c -> addSubscriptions(c.getMeetingEntities(), globalNotifications, subscription.getEmail()));
                }

                // When user use global notification or it's a lecturer without account
                else if (subscription.isGlobal()) {
                    v.forEach(c -> addSubscriptions(
                            filterByFullName(c.getMeetingEntities(), subscription.getLecturer().getFullName()),
                            globalNotifications, subscription.getEmail())
                    );
                }

                // When it's user with local notification
                else {
                    List<NotificationEntity> userNotifications = notificationEntities
                            .stream()
                            .filter(notification -> notification.checkUser(subscription.getUser().getId()))
                            .collect(toList());

                    v.forEach(c -> addSubscriptions(
                            filterByFullName(c.getMeetingEntities(), subscription.getLecturer().getFullName()),
                            userNotifications, subscription.getEmail())
                    );
                }
            }

        });
    }

    public void addSubscriptions(Set<MeetingEntity> meetings, List<NotificationEntity> notifications, String email) {
        for (MeetingEntity meeting : meetings) {
            for (NotificationEntity notification : notifications) {
                Meeting m = meeting.buildMeetingNotification();
                emailQueue.add(meeting.getId(), m);
                emailQueue.addTimetable(meeting.getId(), notification.getUnit(), notification.getValue(), email);
            }
        }
    }

    public Set<MeetingEntity> filterByFullName(Set<MeetingEntity> meetings, String fullName) {
        return meetings.stream()
                .filter(m -> m.getFullName().equals(fullName))
                .collect(Collectors.toSet());
    }

}
