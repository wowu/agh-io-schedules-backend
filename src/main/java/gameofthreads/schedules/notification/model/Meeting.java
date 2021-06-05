package gameofthreads.schedules.notification.model;

import gameofthreads.schedules.entity.MeetingFormat;
import gameofthreads.schedules.entity.MeetingType;

import java.time.LocalDateTime;

public final class Meeting implements Comparable<Meeting> {
    public final LocalDateTime dateStart;
    public final LocalDateTime dateEnd;
    public final String subject;
    public final String group;
    public final String lecturerName;
    public final String lecturerSurname;
    public final MeetingType type;
    public final Integer lengthInHours;
    public final MeetingFormat format;
    public final String room;

    public Meeting(LocalDateTime dateStart, LocalDateTime dateEnd, String subject, String group,
                   String lecturerName, String lecturerSurname, MeetingType type, Integer lengthInHours,
                   MeetingFormat format, String room) {

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
    public int compareTo(Meeting meeting) {
        return dateStart.compareTo(meeting.dateStart);
    }

    public LocalDateTime minusMinutes(Integer minutes) {
        return dateStart.minusMinutes(minutes);
    }

    public String getFullName() {
        return lecturerName + " " + lecturerSurname;
    }

}
