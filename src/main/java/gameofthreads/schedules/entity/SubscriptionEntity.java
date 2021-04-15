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

    @ManyToOne
    @JoinColumn(name = "conference_id")
    private ConferenceEntity conference;

}
