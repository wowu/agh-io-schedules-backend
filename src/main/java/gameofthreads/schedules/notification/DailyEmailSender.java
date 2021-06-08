package gameofthreads.schedules.notification;

import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.SubscriptionEntity;
import gameofthreads.schedules.repository.LecturerRepository;
import gameofthreads.schedules.repository.MeetingRepository;
import gameofthreads.schedules.repository.SubscriptionRepository;
import gameofthreads.schedules.util.HtmlCreator;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class DailyEmailSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(DailyEmailSender.class);

    private final EmailSender emailSender;
    private final MeetingRepository meetingRepository;
    private final LecturerRepository lecturerRepository;
    private final SubscriptionRepository subscriptionRepository;

    public DailyEmailSender(EmailSender emailSender, MeetingRepository meetingRepository,
                            LecturerRepository lecturerRepository, SubscriptionRepository subscriptionRepository) {

        this.emailSender = emailSender;
        this.meetingRepository = meetingRepository;
        this.lecturerRepository = lecturerRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    private Tuple2<LocalDateTime, LocalDateTime> getNextDay() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(1)
                .withHour(0).withMinute(0).withSecond(0);

        final LocalDateTime endDate = startDate.plusDays(1);
        return Tuple.of(startDate, endDate);
    }

    private Map<String, List<MeetingEntity>> prepareMeetings() {
        Map<String, List<MeetingEntity>> emails = new HashMap<>();

        final Map<String, LecturerEntity> lecturers = lecturerRepository
                .findAll()
                .stream()
                .collect(Collectors.toMap(LecturerEntity::getFullName, Function.identity()));

        final var tomorrow = getNextDay();
        final var tomorrowMeetings = meetingRepository.findTomorrowsMeetings(tomorrow._1(), tomorrow._2());

        for (MeetingEntity meeting : tomorrowMeetings) {
            if (lecturers.containsKey(meeting.getFullName())) {
                String email = lecturers.get(meeting.getFullName()).getEmail();
                if (!emails.containsKey(email)) {
                    List<MeetingEntity> meetingEntities = new ArrayList<>();
                    meetingEntities.add(meeting);
                    emails.put(email, meetingEntities);
                } else {
                    emails.get(email).add(meeting);
                }
            }
        }

        return emails;
    }

    private Map<String, Set<ConferenceEntity>> prepareConferences() {
        final var tomorrow = getNextDay();

        final Map<String, List<Set<ConferenceEntity>>> subscriptions = subscriptionRepository
                .fetchAll(tomorrow._1(), tomorrow._2())
                .stream()
                .collect(Collectors.groupingBy(
                        SubscriptionEntity::getEmail,
                        Collectors.mapping(s -> s.getSchedule().getConferences(), toList()))
                );

        Map<String, Set<ConferenceEntity>> flattedSubscriptions = new HashMap<>();

        subscriptions.forEach((email, listOfSets) -> {
            var conferences = listOfSets.stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            flattedSubscriptions.put(email, conferences);
        });

        return flattedSubscriptions;
    }

    @Scheduled(cron = "0 0 8 *  * ?")
    public void sendEmails() {
        LOGGER.info("!! Schedule is running : send email with meetings !!");
        var meetingsPerEmail = prepareMeetings();

        meetingsPerEmail.forEach((email, meetings) -> {
            String htmlMessage = HtmlCreator.createMeetingsEmail(meetings);
            emailSender.sendEmail(email, htmlMessage);
        });

        LOGGER.info("!! Schedule is running : send email with conferences !!");
        var conferencesPerEmail = prepareConferences();

        conferencesPerEmail.forEach((email, conferences) -> {
            if (!meetingsPerEmail.containsKey(email)) {
                String htmlMessage = HtmlCreator.createConferencesEmail(conferences);
                emailSender.sendEmail(email, htmlMessage);
            }
        });

    }

}
