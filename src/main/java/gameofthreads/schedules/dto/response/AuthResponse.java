package gameofthreads.schedules.dto.response;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    public final String token;
    public final String refreshToken;

    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
