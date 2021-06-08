package gameofthreads.schedules.notification.model;

import gameofthreads.schedules.entity.MeetingFormat;
import gameofthreads.schedules.entity.MeetingType;
import gameofthreads.schedules.entity.TimeUnit;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

public final class Meeting implements Comparable<Meeting> {
    final LocalDateTime dateStart;
    public final LocalDateTime dateEnd;
    public final String subject;
    public final String group;
    public final String lecturerName;
    public final String lecturerSurname;
    public final MeetingType type;
    public final Integer lengthInHours;
    public final MeetingFormat format;
    public final String room;

    private final TreeSet<Timetable> timetables = new TreeSet<>();

    public Meeting(LocalDateTime dateStart, LocalDateTime dateEnd, String subject, String group, String lecturerName,
                   String lecturerSurname, MeetingType type, Integer lengthInHours, MeetingFormat format, String room) {

        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.subject = subject;
        this.group = group;
        this.lecturerName = lecturerName;
        this.lecturerSurname = lecturerSurname;
        this.type = type;
        this.lengthInHours = lengthInHours;
        this.format = format;
        this.room = room;
    }

    @Override
    public int compareTo(@NonNull Meeting meeting) {
        return Comparator.comparing(Meeting::getDateStart)
                .thenComparing(Meeting::getFullName)
                .compare(this, meeting);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return Objects.equals(dateStart, meeting.dateStart) &&
                Objects.equals(dateEnd, meeting.dateEnd) &&
                Objects.equals(subject, meeting.subject) &&
                Objects.equals(group, meeting.group) &&
                Objects.equals(lecturerName, meeting.lecturerName) &&
                Objects.equals(lecturerSurname, meeting.lecturerSurname) &&
                type == meeting.type &&
                Objects.equals(lengthInHours, meeting.lengthInHours) &&
                format == meeting.format &&
                Objects.equals(room, meeting.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateStart, dateEnd, subject, group, lecturerName, lecturerSurname, type, lengthInHours, format, room);
    }

    public String getFullName() {
        return lecturerName + " " + lecturerSurname;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public TreeSet<Timetable> getTimetables() {
        return timetables;
    }

    public boolean addTimetable(TimeUnit timeUnit, Integer timeValue, String email) {
        LocalDateTime sendTime = getDateStart().minusMinutes((long) timeUnit.getMinutes() * timeValue);
        if(sendTime.isAfter(LocalDateTime.now())) {
            timetables.add(new Timetable(email, sendTime));
            return true;
        }
        return false;
    }

    public boolean hasZeroTimetable() {
        return timetables.size() == 0;
    }

    public Optional<Timetable> first(){
        LocalDateTime now = LocalDateTime.now();
        Timetable first = timetables.first();

        while(!now.isBefore(first.getLocalDateTime())){
            timetables.pollFirst();

            if(timetables.size() == 0){
                return Optional.empty();
            }

            first = timetables.first();
        }

        return Optional.of(first);
    }

    public boolean isTimeToSend(){
        if(timetables.size() == 0){
            return false;
        }

        return timetables.first().isTimeToSend();
    }

}
