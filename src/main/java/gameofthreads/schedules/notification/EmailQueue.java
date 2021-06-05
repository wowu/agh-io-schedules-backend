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
        concurrentHashMap.get(scheduleId).addDetails(email, details);
    }

    public Notification update(Integer scheduleId) {
        return concurrentHashMap.get(scheduleId);
    }

    public void delete(Integer scheduleId, String email) {
        concurrentHashMap.get(scheduleId).deleteDetails(email);
    }

    public void clear() {
        concurrentHashMap.clear();
    }

    public Optional<Pair<TreeSet<Conference>, Schedule>> pop() {
        final LocalDateTime future = LocalDateTime.of(2100, 10, 10, 10, 10);

        TreeSet<Conference> conferences = new TreeSet<>();

        Schedule schedule = null;
        Integer scheduleToModify = null;
        String emailToRemove = null;

        for (Map.Entry<Integer, Notification> entry : concurrentHashMap.entrySet()) {
            Schedule firstEmailToSend = entry.getValue().getSchedules().first();
            if (future.isAfter(firstEmailToSend.getLocalDateTime())) {
                conferences = entry.getValue().getConference();
                schedule = firstEmailToSend;
                scheduleToModify = entry.getKey();
                emailToRemove = firstEmailToSend.getEmail();
            }
        }

        if (schedule != null && schedule.isTimeToSend()) {
            delete(scheduleToModify, emailToRemove);
            return Optional.of(Pair.of(conferences, schedule));
        }

        return Optional.empty();
    }

    public Integer size() {
        int size = 0;

        for (Map.Entry<Integer, Notification> entry : concurrentHashMap.entrySet()) {
            System.out.println(entry.getValue().getSchedules().size());
            if (entry.getValue().getSchedules().size() > 0) {
                size++;
            }
        }

        return size;
    }

}
