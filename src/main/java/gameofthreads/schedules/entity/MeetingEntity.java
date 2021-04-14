package gameofthreads.schedules.entity;

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

    @Column(name = "lecturer")
    private String lecturer;

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
                         String groupName, String lecturer, MeetingType type, Integer lengthInHours,
                         MeetingFormat format, String room) {
        this.conference = conference;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.subject = subject;
        this.groupName = groupName;
        this.lecturer = lecturer;
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

    public String getLecturer() {
        return lecturer;
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
}
