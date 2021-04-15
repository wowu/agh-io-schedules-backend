package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    private List<MeetingEntity> meetingEntities;

    @OneToMany(mappedBy = "conference")
    private Set<SubscriptionEntity> subscriptions;

    @Column(name = "public_link")
    private String publicLink;

    public ConferenceEntity() {
    }

    public ConferenceEntity(ScheduleEntity schedule, String publicLink) {
        this.schedule = schedule;
        this.publicLink = publicLink;
        this.meetingEntities = new ArrayList<>();
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

    public String getPublicLink() {
        return publicLink;
    }

}
