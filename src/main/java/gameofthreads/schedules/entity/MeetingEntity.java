package gameofthreads.schedules.entity;

import gameofthreads.schedules.notification.model.Meeting;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting")
public class MeetingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id")
    private ConferenceEntity conference;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @Column(name = "subject")
    private String subject;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "lecturer_name")
    private String lecturerName;

    @Column(name = "lecturer_surname")
    private String lecturerSurname;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MeetingType type;

    @Column(name = "length_in_hours")
    private Integer lengthInHours;

    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private MeetingFormat format;

    @Column(name = "room")
    private String room;

    public MeetingEntity() {

    }

    public MeetingEntity(ConferenceEntity conference, LocalDateTime dateStart, LocalDateTime dateEnd, String subject,
                         String groupName, String lecturerName, String lecturerSurname, MeetingType type, Integer lengthInHours,
                         MeetingFormat format, String room) {
        this.conference = conference;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.subject = subject;
        this.groupName = groupName;
        this.lecturerName = lecturerName;
        this.lecturerSurname = lecturerSurname;
        this.type = type;
        this.lengthInHours = lengthInHours;
        this.format = format;
        this.room = room;
    }

    public Integer getId() {
        return id;
    }

    public ConferenceEntity getConferenceEntity() {
        return conference;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public String getSubject() {
        return subject;
    }

    public String getGroup() {
        return groupName;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getLecturerSurname() {
        return lecturerSurname;
    }

    public String getFullName() {
        return lecturerName + " " + lecturerSurname;
    }

    public MeetingType getType() {
        return type;
    }

    public Integer getLengthInHours() {
        return lengthInHours;
    }

    public MeetingFormat getFormat() {
        return format;
    }

    public String getRoom() {
        return room;
    }

    public Meeting buildMeetingNotification() {
        return new Meeting(
                dateStart,
                dateEnd,
                subject,
                groupName,
                lecturerName,
                lecturerSurname,
                type,
                lengthInHours,
                format,
                room
        );
    }

}
