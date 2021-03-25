package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name="account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "password")
    private String password;

}
