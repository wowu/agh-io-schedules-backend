package gameofthreads.schedules.dto.request;

import java.io.Serializable;

public class AddUserRequest implements Serializable {
    public final String email;
    public final String password;

    public AddUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
