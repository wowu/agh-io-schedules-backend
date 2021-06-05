package gameofthreads.schedules.notification.model;

import java.time.LocalDateTime;

public final class Schedule implements Comparable<Schedule> {
    final String email;
    final LocalDateTime localDateTime;
    final boolean fullNotification;

    public Schedule(String email, LocalDateTime localDateTime, boolean fullNotification) {
        this.email = email;
        this.localDateTime = localDateTime;
        this.fullNotification = fullNotification;
    }

    @Override
    public int compareTo(Schedule schedule) {
        return localDateTime.compareTo(schedule.localDateTime);
    }

}
