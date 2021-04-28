package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.LecturerEntity;

import java.io.Serializable;

public class LecturerResponse implements Serializable {
    public final int id;
    public final String name;
    public final String surname;
    public final String email;
    public final boolean activeSubscription;

    public LecturerResponse(LecturerEntity lecturerEntity) {
        this.id = lecturerEntity.getId();
        this.name = lecturerEntity.getName();
        this.surname = lecturerEntity.getSurname();
        this.email = lecturerEntity.getEmail();
        this.activeSubscription = lecturerEntity.isActiveSubscription();
    }

}
