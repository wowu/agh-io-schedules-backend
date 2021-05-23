package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.ScheduleEntity;

import java.io.Serializable;
import java.util.List;

public class LecturerShortResponse implements Serializable {
    public final int id;
    public final String name;
    public final String surname;
    public final String email;

    public LecturerShortResponse(LecturerEntity lecturerEntity) {
        this.id = lecturerEntity.getId();
        this.name = lecturerEntity.getName();
        this.surname = lecturerEntity.getSurname();
        this.email = lecturerEntity.getEmail();
    }
}
