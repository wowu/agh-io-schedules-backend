package gameofthreads.schedules.notification;

import gameofthreads.schedules.notification.model.Conference;
import gameofthreads.schedules.notification.model.Notification;
import gameofthreads.schedules.notification.model.Schedule;
import gameofthreads.schedules.notification.model.ScheduleDetails;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

        Schedule schedule = null;
        Map.Entry<Integer, Notification> chosenEntry = null;

        for (Map.Entry<Integer, Notification> entry : concurrentHashMap.entrySet()) {
            TreeSet<Schedule> subscriptionsPerSchedule = entry.getValue().getSchedules();

            if (subscriptionsPerSchedule.size() == 0) {
                continue;
            }

            Schedule firstSubscription = subscriptionsPerSchedule.first();

            while (now.isAfter(firstSubscription.getLocalDateTime())) {
                entry.getValue().getSchedules().pollFirst();
                firstSubscription = entry.getValue().getSchedules().first();
                if (firstSubscription == null) break;
            }

            if (firstSubscription == null) continue;

            if (future.isAfter(firstSubscription.getLocalDateTime())) {
                future = firstSubscription.getLocalDateTime();
                schedule = firstSubscription;
                chosenEntry = entry;
            }
        }

        if (schedule != null && schedule.isTimeToSend()) {
            System.out.println(schedule.isFullNotification());
            System.out.println(schedule.getLocalDateTime());
            System.out.println(schedule.getEmail());
            TreeSet<Conference> conferences = chosenEntry.getValue().getConference();
            chosenEntry.getValue().getSchedules().pollFirst();
            return Optional.of(Pair.of(conferences, schedule));
        }

        return Optional.empty();
    }

    public ConcurrentHashMap<Integer, Notification> getConcurrentHashMap() {
        return concurrentHashMap;
    }

}
