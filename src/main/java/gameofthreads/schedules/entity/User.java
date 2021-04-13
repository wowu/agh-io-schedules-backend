package gameofthreads.schedules.entity;

import javax.persistence.*;

@Entity
@Table(name = "my_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public Integer getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return person.getEmail();
    }

}
