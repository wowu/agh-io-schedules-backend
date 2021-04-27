package gameofthreads.schedules.dto.request;

import java.io.Serializable;

public class AddUserRequest implements Serializable {
    public final String email;
    public final String password;
    public final boolean activeSubscription;

    public AddUserRequest(String email, String password, boolean activeSubscription) {
        this.email = email;
        this.password = password;
        this.activeSubscription = activeSubscription;
    }

}
