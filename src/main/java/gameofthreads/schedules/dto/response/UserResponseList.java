package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class UserResponseList implements Serializable {
    public final List<UserResponse> users;

    public UserResponseList(List<UserResponse> users) {
        this.users = users;
    }

}
