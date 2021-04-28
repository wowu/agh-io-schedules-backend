package gameofthreads.schedules.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "email")
public class EmailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @OneToOne(mappedBy = "email", cascade = CascadeType.ALL)
    private LecturerEntity lecturer;

    @OneToOne(mappedBy = "email", cascade = CascadeType.ALL)
    private UserEntity user;

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL)
    private Set<SubscriptionEntity> subscription;

    public Integer getId() {
        return id;
    }

    public EmailEntity() {
    }

    public EmailEntity(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LecturerEntity getLecturer() {
        return lecturer;
    }
}
