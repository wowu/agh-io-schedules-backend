package gameofthreads.schedules.entity;

import gameofthreads.schedules.dto.request.AddLecturerRequest;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "lecturer")
public class LecturerEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "subscriptions")
    private boolean activeSubscription;

    public LecturerEntity() {
    }

    public LecturerEntity(AddLecturerRequest addLecturerRequest) {
        this.email = addLecturerRequest.email;
        this.name = addLecturerRequest.name;
        this.surname = addLecturerRequest.surname;
        this.activeSubscription = addLecturerRequest.activeSubscription;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setActiveSubscription(boolean activeSubscription) {
        this.activeSubscription = activeSubscription;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isActiveSubscription() {
        return activeSubscription;
    }

}
