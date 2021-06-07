package gameofthreads.schedules.notification.model;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class Notification {
    private final TreeSet<Conference> conference = new TreeSet<>();
    private final TreeSet<Schedule> schedules = new TreeSet<>();

    public Notification(List<Conference> conference) {
        this.conference.addAll(conference);
    }

    public void addSchedule(String email, ScheduleDetails details) {
        schedules.add(new Schedule(email, createSendTime(details.timeInMinute(), details.getFullName()), details.fullNotifications));
    }

    public void deleteSchedule(String email){
        schedules.removeIf(schedule -> email.equals(schedule.email));
    }

    private LocalDateTime createSendTime(Integer minutes, String fullName) {
        if(!fullName.equals("")) {
            Iterator<Conference> itr = conference.iterator();
            while (itr.hasNext()) {
                for(Meeting meeting : itr.next().getMeetings()){
                    if(meeting.getFullName().equals(fullName)){
                        return meeting.minusMinutes(minutes);
                    }
                }
            }
        }

        return conference.first().calculateSendTime(minutes);
    }

    public TreeSet<Conference> getConference() {
        return conference;
    }

    public TreeSet<Schedule> getSchedules() {
        return schedules;
    }

}
