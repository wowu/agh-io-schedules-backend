package gameofthreads.schedules.notification;

import gameofthreads.schedules.notification.model.Notification;
import gameofthreads.schedules.notification.model.ScheduleDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmailQueue {
    private ConcurrentHashMap<Integer, Notification> concurrentHashMap = new ConcurrentHashMap<>();

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

    public void clear(){
        concurrentHashMap = new ConcurrentHashMap<>();
    }

}
