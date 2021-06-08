package gameofthreads.schedules.notification;

import gameofthreads.schedules.notification.model.*;
import gameofthreads.schedules.util.HtmlCreator;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class EmailSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    private final EmailQueue emailQueue;
    private final JavaMailSender javaMailSender;

    public EmailSender(EmailQueue emailQueue, JavaMailSender javaMailSender) {
        this.emailQueue = emailQueue;
        this.javaMailSender = javaMailSender;
    }

    @Scheduled(initialDelay = 1000 * 30, fixedDelay = 100)
    public void run() {
        var optionalMeeting = emailQueue.pop();
        if (optionalMeeting.isPresent()) {
            var meeting = optionalMeeting.get();

            Timetable timetable = meeting.first().orElse(null);

            if(timetable != null){
                String html = HtmlCreator.createContext(meeting);
                sendEmail(timetable.getEmail(), html);
            }
        }
    }

    private void sendEmail(String email, String htmlMessage) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        Try<Void> setEmail = Try.run(() -> helper.setTo(email));
        Try<Void> setSubject = Try.run(() -> helper.setSubject("Przypomnienie o Wydarzeniu"));
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
