package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name = "subscription")
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "active")
    public boolean active = true;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    public SubscriptionEntity(String email, ScheduleEntity schedule) {
        this.email = email;
        this.schedule = schedule;
    }

    public SubscriptionEntity() {
    }

    public ScheduleEntity getSchedule() {
        return schedule;
    }

}
