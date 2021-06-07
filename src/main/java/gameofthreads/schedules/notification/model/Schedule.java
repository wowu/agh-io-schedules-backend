package gameofthreads.schedules.notification.model;

import java.time.LocalDateTime;
import java.util.Comparator;

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
        return Comparator.comparing(Schedule::getLocalDateTime)
                .thenComparing(Schedule::getEmail)
                .compare(this, schedule);
    }

    public boolean isTimeToSend() {
        final int DELAY_IN_SECOND = 60;
        boolean isBefore = localDateTime.isBefore(LocalDateTime.now().plusSeconds(DELAY_IN_SECOND));
        boolean isAfter = localDateTime.isAfter(LocalDateTime.now().minusSeconds(DELAY_IN_SECOND));
        return isAfter && isBefore;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public boolean isFullNotification() {
        return fullNotification;
    }

}
