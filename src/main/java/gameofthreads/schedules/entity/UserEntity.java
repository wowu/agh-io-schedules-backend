package gameofthreads.schedules.entity;

import gameofthreads.schedules.dto.request.AddUserRequest;

import javax.persistence.*;

@Entity
@Table(name = "my_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecturer_id")
    private LecturerEntity lecturer;

    public UserEntity() {
    }

    public UserEntity(AddUserRequest addUserRequest) {
        this.password = addUserRequest.password;
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

    public Role getRole() {
        return role;
    }

    public LecturerEntity getLecturer() {
        return lecturer;
    }

    public void setLecturer(LecturerEntity lecturer) {
        this.lecturer = lecturer;
    }
}
