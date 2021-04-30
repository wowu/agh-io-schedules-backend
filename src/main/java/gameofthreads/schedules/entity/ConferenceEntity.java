package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conference")
public class ConferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL)
    private Set<MeetingEntity> meetingEntities;

    public ConferenceEntity() {
    }

    public ConferenceEntity(ScheduleEntity schedule) {
        this.schedule = schedule;
        this.meetingEntities = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public ScheduleEntity getScheduleEntity() {
        return schedule;
    }

    public Set<MeetingEntity> getMeetingEntities() {
        return meetingEntities;
    }

    public void setSchedule(ScheduleEntity schedule) {
        this.schedule = schedule;
    }
}
