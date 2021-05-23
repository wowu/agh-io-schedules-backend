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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "email_id")
    private EmailEntity email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    public LecturerEntity() {
    }

    public LecturerEntity(AddLecturerRequest addLecturerRequest, EmailEntity emailEntity) {
        this.email = emailEntity;
        this.name = addLecturerRequest.name;
        this.surname = addLecturerRequest.surname;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email.getEmail();
    }

    public void setEmail(EmailEntity email) {
        this.email = email;
    }

    public EmailEntity getEmailEntity() {
        return email;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

}
