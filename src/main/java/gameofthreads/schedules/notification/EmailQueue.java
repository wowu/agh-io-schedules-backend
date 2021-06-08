package gameofthreads.schedules.notification;

import gameofthreads.schedules.entity.TimeUnit;
import gameofthreads.schedules.notification.model.Meeting;
import gameofthreads.schedules.notification.model.Timetable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmailQueue {
    private final ConcurrentHashMap<Integer, Meeting> queue = new ConcurrentHashMap<>();

    public void add(Integer id, Meeting meeting) {
        queue.putIfAbsent(id, meeting);
    }

    public void addTimetable(Integer id, TimeUnit timeUnit, Integer timeValue, String email) {
        queue.get(id).addTimetable(timeUnit, timeValue, email);
    }

    public ConcurrentHashMap<Integer, Meeting> getQueue() {
        return queue;
    }

    public void clear() {
        queue.clear();
    }

    public Optional<Meeting> pop() {
        LocalDateTime future = LocalDateTime.of(2100, 10, 10, 10, 10);
        Meeting top = null;

        for(Map.Entry<Integer, Meeting> entry : queue.entrySet()){
            Meeting meeting = entry.getValue();

            if(meeting.hasZeroTimetable()){
                continue;
            }

            if(meeting.first().isPresent()){
                LocalDateTime localDateTime = meeting.first().map(Timetable::getLocalDateTime).get();
                if(localDateTime.isBefore(future)){
                    future = localDateTime;
                    top = meeting;
                }
            }
        }

        if(top != null && top.isTimeToSend()){
            top.getTimetables().pollFirst();
            return Optional.of(top);
        }

        return Optional.empty();
    }

}
