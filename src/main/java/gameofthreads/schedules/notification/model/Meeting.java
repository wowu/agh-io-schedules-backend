package gameofthreads.schedules.notification.model;

import gameofthreads.schedules.entity.MeetingFormat;
import gameofthreads.schedules.entity.MeetingType;

import java.time.LocalDateTime;

public final class Meeting implements Comparable<Meeting> {
    final LocalDateTime dateStart;
    final LocalDateTime dateEnd;
    final String subject;
    final String group;
    final String lecturerName;
    final String lecturerSurname;
    final MeetingType type;
    final Integer lengthInHours;
    final MeetingFormat format;
    final String room;

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

}
