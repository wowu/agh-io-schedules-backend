package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.EmailEntity;

import java.io.Serializable;

public class UserResponse implements Serializable {
    public final Integer id;
    public final String email;

    public UserResponse(EmailEntity emailEntity) {
        this.id = emailEntity.getUser().getId();
        this.email = emailEntity.getEmail();
    }

    public UserResponse(Integer id, String email) {
        this.id = id;
        this.email = email;
    }

}
