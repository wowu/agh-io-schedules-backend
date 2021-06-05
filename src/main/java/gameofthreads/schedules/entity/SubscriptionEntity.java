package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name = "subscription")
public class SubscriptionEntity {
    @Column(name = "active")
    public boolean active = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "email_id")
    private EmailEntity email;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    public SubscriptionEntity(EmailEntity email, ScheduleEntity schedule) {
        this.email = email;
        this.schedule = schedule;
    }

    public SubscriptionEntity() {
    }

    public ScheduleEntity getSchedule() {
        return schedule;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email.getEmail();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserEntity getUser() {
        return email.getUser();
    }

    public boolean isGlobal() {
        return email.getUser().isGlobalNotifications();
    }

    public boolean isActive() {
        return active;
    }

}
