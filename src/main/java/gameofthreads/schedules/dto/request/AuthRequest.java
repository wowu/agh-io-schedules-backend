package gameofthreads.schedules.dto.request;

import java.io.Serializable;

public class AuthRequest implements Serializable {
    public final String username;
    public final String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
