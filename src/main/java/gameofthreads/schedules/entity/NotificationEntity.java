package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name = "notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private TimeUnit unit;

    @Column(name = "value")
    private Integer value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public NotificationEntity() { }

    public NotificationEntity(Integer id, TimeUnit unit, Integer value, UserEntity user) {
        this.id = id;
        this.unit = unit;
        this.value = value;
        this.user = user;
    }

}
