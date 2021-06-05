package gameofthreads.schedules.notification.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

public final class Conference implements Comparable<Conference> {
    private final TreeSet<Meeting> meetings = new TreeSet<>();

    public Conference(List<Meeting> meetings) {
        this.meetings.addAll(meetings);
    }

    @Override
    public int compareTo(Conference conference) {
        return meetings.first().compareTo(conference.meetings.first());
    }

    public LocalDateTime calculateSendTime(Integer minutes) {
        return meetings.first().minusMinutes(minutes);
    }

    public TreeSet<Meeting> getMeetings() {
        return meetings;
    }

}
