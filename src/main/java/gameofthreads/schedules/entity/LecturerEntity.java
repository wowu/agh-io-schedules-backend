package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name = "lecturer")
public class LecturerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "subscriptions")
    private boolean activeSubscription;
}
