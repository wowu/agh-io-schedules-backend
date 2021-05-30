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

    public LecturerEntity(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.email = null;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return (email == null) ? "" : email.getEmail();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((surname == null) ? 0 : surname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LecturerEntity other = (LecturerEntity) obj;

        return name.equals(other.name) && surname.equals(other.surname);
    }

    public void updateEmail(String email){
        if(getEmailEntity() == null){
            this.email = new EmailEntity(email);
        }else{
            this.email.setEmail(email);
        }
    }

}
