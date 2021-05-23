package gameofthreads.schedules.dto.request;

import java.io.Serializable;

public class AddLecturerRequest implements Serializable {
    public final String name;
    public final String surname;
    public final String email;

    public AddLecturerRequest(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

}
