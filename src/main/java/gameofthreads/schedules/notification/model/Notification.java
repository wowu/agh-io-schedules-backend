package gameofthreads.schedules.notification.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

public class Notification {
    private final TreeSet<Conference> conference = new TreeSet<>();
    private final TreeSet<Schedule> schedules = new TreeSet<>();

    public Notification(List<Conference> conference) {
        this.conference.addAll(conference);
    }

    public void addDetails(String email, ScheduleDetails details) {
        schedules.add(new Schedule(email, createSendTime(details.timeInMinute()), details.fullNotifications));
    }

    public void deleteDetails(String email){
        schedules.removeIf(schedule -> email.equals(schedule.email));
    }

    private LocalDateTime createSendTime(Integer minutes) {
        return conference.first().calculateSendTime(minutes);
    }

}