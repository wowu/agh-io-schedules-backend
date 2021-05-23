package gameofthreads.schedules.service;

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
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class EmailService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;
    private final MeetingRepository meetingRepository;
    private final LecturerRepository lecturerRepository;
    private final SubscriptionRepository subscriptionRepository;

    public EmailService(JavaMailSender javaMailSender, MeetingRepository meetingRepository,
                        LecturerRepository lecturerRepository, SubscriptionRepository subscriptionRepository) {

        this.javaMailSender = javaMailSender;
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

    @Scheduled(cron = "0 0 19 *  * ?")
    public void sendEmails() {
        LOGGER.info("!! Schedule is running : send email with meetings !!");
        var meetingsPerEmail = prepareMeetings();

        meetingsPerEmail.forEach((email, meetings) -> {
            String htmlMessage = HtmlCreator.createMeetingsEmail(meetings);
            sendEmail(email, htmlMessage);
        });

        LOGGER.info("!! Schedule is running : send email with conferences !!");
        var conferencesPerEmail = prepareConferences();

        conferencesPerEmail.forEach((email, conferences) -> {
            if (!meetingsPerEmail.containsKey(email)) {
                String htmlMessage = HtmlCreator.createConferencesEmail(conferences);
                sendEmail(email, htmlMessage);
            }
        });

    }

    private void sendEmail(String email, String htmlMessage) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        Try<Void> setEmail = Try.run(() -> helper.setTo(email));
        Try<Void> setSubject = Try.run(() -> helper.setSubject("Przypomnienie o Konferencji"));
        Try<Void> setMessage = Try.run(() -> helper.setText(htmlMessage, true));

        if (setMessage.isSuccess() && setSubject.isSuccess() && setMessage.isSuccess()) {
            javaMailSender.send(message);
        } else {
            LOGGER.error(
                    "Error during send email to : " + email,
                    setEmail.getCause(),
                    setMessage.getCause(),
                    setSubject.getCause()
            );
        }
    }

}
