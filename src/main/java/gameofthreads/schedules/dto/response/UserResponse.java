package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.EmailEntity;
import gameofthreads.schedules.entity.UserEntity;

import java.io.Serializable;

public class UserResponse implements Serializable {
    public final Integer id;
    public final String email;
    public final boolean activeSubscription;

    public UserResponse(EmailEntity emailEntity) {
        this.id = emailEntity.getUser().getId();
        this.email = emailEntity.getEmail();
        this.activeSubscription = emailEntity.getLecturer().isActiveSubscription();
    }

}
