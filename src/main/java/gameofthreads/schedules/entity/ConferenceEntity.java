package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "conference")
public class ConferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    @OneToMany(mappedBy = "conference")
    private List<MeetingEntity> meetingEntities;

    public ConferenceEntity() {
    }

    public ConferenceEntity(ScheduleEntity schedule) {
        this.schedule = schedule;
    }

    public Integer getId() {
        return id;
    }

    public ScheduleEntity getScheduleEntity() {
        return schedule;
    }

    public List<MeetingEntity> getMeetingEntities() {
        return meetingEntities;
    }
}
