package gameofthreads.schedules.notification.model;

import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Comparator;

public final class Timetable implements Comparable<Timetable> {
    final String email;
    final LocalDateTime localDateTime;

    public Timetable(String email, LocalDateTime localDateTime) {
        this.email = email;
        this.localDateTime = localDateTime;
    }

    @Override
    public int compareTo(@NonNull Timetable schedule) {
        return Comparator.comparing(Timetable::getLocalDateTime)
                .thenComparing(Timetable::getEmail)
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

}
