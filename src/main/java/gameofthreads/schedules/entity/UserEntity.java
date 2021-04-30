package gameofthreads.schedules.entity;

import gameofthreads.schedules.dto.request.AddUserRequest;

import javax.persistence.*;

@Entity
@Table(name = "my_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "email_id")
    private EmailEntity email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public UserEntity() {
    }

    public UserEntity(String password) {
        this.password = password;
        this.role = Role.LECTURER;
    }

    public Integer getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(EmailEntity email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public EmailEntity getEmailEntity() {
        return email;
    }
}
