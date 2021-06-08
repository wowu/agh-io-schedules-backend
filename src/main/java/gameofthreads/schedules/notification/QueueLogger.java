package gameofthreads.schedules.notification;

import gameofthreads.schedules.notification.model.Meeting;
import gameofthreads.schedules.notification.model.Timetable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class QueueLogger {
    private final Logger LOGGER = LoggerFactory.getLogger(QueueLogger.class);
    private final EmailQueue emailQueue;

    public QueueLogger(EmailQueue emailQueue) {
        this.emailQueue = emailQueue;
    }

    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 1000 * 60 * 2)
    public void monitorQueue() {
        LOGGER.info("MONITORING QUEUE START");

        for (Map.Entry<Integer, Meeting> entry : emailQueue.getQueue().entrySet()) {
            LOGGER.info("Meeting ID : " + entry.getKey());
            for (Timetable timetable : entry.getValue().getTimetables()) {
                LOGGER.info(
                        MessageFormat.format("Email {0}; Time {1}",
                                timetable.getEmail(),
                                timetable.getLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )
                );
            }
        }

        LOGGER.info("MONITORING QUEUE END");
    }

}
