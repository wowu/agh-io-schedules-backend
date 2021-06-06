package gameofthreads.schedules.notification;

import gameofthreads.schedules.notification.model.Conference;
import gameofthreads.schedules.notification.model.Notification;
import gameofthreads.schedules.notification.model.Schedule;
import gameofthreads.schedules.notification.model.ScheduleDetails;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmailQueue {
    private final ConcurrentHashMap<Integer, Notification> concurrentHashMap = new ConcurrentHashMap<>();

    public void add(Integer scheduleId, Notification notification) {
        concurrentHashMap.putIfAbsent(scheduleId, notification);
    }

    public void updateDetails(Integer scheduleId, String email, ScheduleDetails details) {
        concurrentHashMap.get(scheduleId).addSchedule(email, details);
    }

    public Notification update(Integer scheduleId) {
        return concurrentHashMap.get(scheduleId);
    }

    public void delete(Integer scheduleId, String email) {
        concurrentHashMap.get(scheduleId).deleteSchedule(email);
    }

    public void clear() {
        concurrentHashMap.clear();
    }

    public Optional<Pair<TreeSet<Conference>, Schedule>> pop() {
        final LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = LocalDateTime.of(2100, 10, 10, 10, 10);

        TreeSet<Conference> conferences = new TreeSet<>();

        Schedule schedule = null;
        Map.Entry<Integer, Notification> chosenEntry = null;

        for (Map.Entry<Integer, Notification> entry : concurrentHashMap.entrySet()) {
            Schedule firstEmailToSend = entry.getValue().getSchedules().first();

            if (now.isAfter(firstEmailToSend.getLocalDateTime())) {
                entry.getValue().getSchedules().pollFirst();
            } else if (future.isAfter(firstEmailToSend.getLocalDateTime())) {
                conferences = entry.getValue().getConference();
                schedule = firstEmailToSend;
                chosenEntry = entry;
                future = firstEmailToSend.getLocalDateTime();
            }
        }

        if (schedule != null && schedule.isTimeToSend()) {
            chosenEntry.getValue().getSchedules().pollFirst();
            return Optional.of(Pair.of(conferences, schedule));
        }

        return Optional.empty();
    }

    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 900)
    public void monitorQueue() {
        for (Map.Entry<Integer, Notification> entry : concurrentHashMap.entrySet()) {
            System.out.println("Schedule ID : " + entry.getKey());
            for (Schedule schedule : entry.getValue().getSchedules()) {
                System.out.println(
                        MessageFormat.format("Email {0}; Time {1}",
                                schedule.getEmail(),
                                schedule.getLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )
                );
            }
        }
    }

}
