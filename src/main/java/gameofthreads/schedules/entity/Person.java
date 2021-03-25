package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name="person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "person")
    private Account account;

}
