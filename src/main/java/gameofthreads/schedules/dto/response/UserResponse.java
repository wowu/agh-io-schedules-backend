package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.UserEntity;

import java.io.Serializable;

public class UserResponse implements Serializable {
    public final Integer id;
    public final String email;
    public final boolean activeSubscription;

    public UserResponse(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.email = userEntity.getLecturer().getEmail();
        this.activeSubscription = userEntity.getLecturer().isActiveSubscription();
    }

}
