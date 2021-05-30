package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.ScheduleEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class LecturerMediumResponse implements Serializable {
    public final int id;
    public final String name;
    public final String surname;
    public final String email;
    public final Integer eventsCount;

    public LecturerMediumResponse(LecturerEntity lecturerEntity, long eventsCount) {
        this.id = lecturerEntity.getId();
        this.name = lecturerEntity.getName();
        this.surname = lecturerEntity.getSurname();
        this.email = lecturerEntity.getEmail();
        this.eventsCount = Math.toIntExact(eventsCount);
    }

}
