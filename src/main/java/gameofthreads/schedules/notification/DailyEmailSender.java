package gameofthreads.schedules.notification;

import gameofthreads.schedules.entity.*;
import gameofthreads.schedules.repository.*;
import gameofthreads.schedules.util.HtmlCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class DailyEmailSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(DailyEmailSender.class);

    private final EmailSender emailSender;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final ConferenceRepository conferenceRepository;

    public DailyEmailSender(EmailSender emailSender, SubscriptionRepository subscriptionRepository,
                            NotificationRepository notificationRepository, ConferenceRepository conferenceRepository) {

        this.emailSender = emailSender;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.conferenceRepository = conferenceRepository;
    }

    private LocalDateTime getAnotherDay(Integer plusDays) {
        return LocalDateTime.now()
                .plusDays(plusDays)
                .withHour(0)
                .withMinute(0)
                .withSecond(30);
    }

    private Map<String, Set<List<MeetingEntity>>> prepareMeetings() {
        Map<String, Set<List<MeetingEntity>>> results = new HashMap<>();

        List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();
        List<NotificationEntity> notifications = notificationRepository.findAll();

        List<NotificationEntity> globalNotifications = notifications
                .stream()
                .filter(NotificationEntity::isGlobal)
                .filter(n -> n.getUnit().equals(TimeUnit.DAY))
                .collect(toList());

        Map<Integer, List<ConferenceEntity>> conferenceGroupedBySchedule = conferenceRepository
                .fetchWithScheduleAndMeetings()
                .stream()
                .collect(groupingBy(conference -> conference.getScheduleEntity().getId()));

        for (SubscriptionEntity subscription : subscriptions) {
            Integer scheduleId = subscription.getSchedule().getId();
            List<MeetingEntity> meetings = conferencesToMeetings(conferenceGroupedBySchedule.get(scheduleId));

            // When add by public link
            if (subscription.getLecturer() == null) {
                for (NotificationEntity notification : globalNotifications) {
                    var anotherDay = getAnotherDay(notification.getValue());
                    List<MeetingEntity> filteredMeetings = meetings.stream()
                            .filter(m -> m.getDateStart().getDayOfYear() == anotherDay.getDayOfYear())
                            .collect(toList());

                    putToResults(results, subscription, filteredMeetings);
                }
            }

            // When user use global notification or it's a lecturer without account
            else if (subscription.isGlobal()) {
                insertMeetings(results, globalNotifications, subscription, meetings);
            }

            // When it's user with local notification
            else {
                List<NotificationEntity> userNotifications = notifications
                        .stream()
                        .filter(notification -> notification.checkUser(subscription.getUser().getId()))
                        .collect(toList());

                insertMeetings(results, userNotifications, subscription, meetings);
            }
        }

        return results;
    }

    private void insertMeetings(Map<String, Set<List<MeetingEntity>>> results, List<NotificationEntity> globalNotifications,
                                SubscriptionEntity subscription, List<MeetingEntity> meetings) {

        for (NotificationEntity notification : globalNotifications) {
            var anotherDay = getAnotherDay(notification.getValue());
            List<MeetingEntity> filteredMeetings = meetings.stream()
                    .filter(m -> m.getFullName().equals(subscription.getLecturer().getFullName()))
                    .filter(m -> m.getDateStart().getDayOfYear() == anotherDay.getDayOfYear())
                    .collect(toList());

            putToResults(results, subscription, filteredMeetings);
        }
    }

    private void putToResults(Map<String, Set<List<MeetingEntity>>> results, SubscriptionEntity subscription,
                              List<MeetingEntity> filteredMeetings) {

        if (filteredMeetings.size() > 0) {
            if (results.containsKey(subscription.getEmail())) {
                results.get(subscription.getEmail()).add(filteredMeetings);
            } else {
                Set<List<MeetingEntity>> set = new LinkedHashSet<>();
                set.add(filteredMeetings);
                results.put(subscription.getEmail(), set);
            }
        }
    }

    private List<MeetingEntity> conferencesToMeetings(List<ConferenceEntity> conferences) {
        List<MeetingEntity> meetings = new ArrayList<>();
        conferences.forEach(c -> meetings.addAll(c.getMeetingEntities()));

        return meetings.stream()
                .sorted(Comparator.comparing(MeetingEntity::getDateStart))
                .collect(toList());
    }

    @Scheduled(cron = "0 30 8 *  * ?")
    public void sendEmails() {
        LOGGER.info("!! Schedule is running : send email with meetings !!");
        var meetingsPerEmail = prepareMeetings();

        meetingsPerEmail.forEach((email, meetings) -> {
            meetings.forEach(m -> {
                String htmlMessage = HtmlCreator.createMeetingsEmail(m);
                emailSender.sendEmail(email, htmlMessage);
            });

        });

    }

}
