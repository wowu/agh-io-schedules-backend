package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.ScheduleEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class LecturerDetailedResponse implements Serializable {
    public final int id;
    public final String name;
    public final String surname;
    public final String email;
    public final Integer eventsCount;
    public final List<DetailedScheduleResponse> schedules;

    public LecturerDetailedResponse(LecturerEntity lecturerEntity, List<ScheduleEntity> schedules) {
        this.id = lecturerEntity.getId();
        this.name = lecturerEntity.getName();
        this.surname = lecturerEntity.getSurname();
        this.email = lecturerEntity.getEmail();
        this.eventsCount = (int) schedules.stream()
                .map(scheduleEntity -> scheduleEntity.getConferences().stream().
                        flatMap(conferenceEntity -> conferenceEntity.getMeetingEntities().stream())
                        .collect(Collectors.toList())).count();
        this.schedules = schedules.stream()
                .map(DetailedScheduleResponse::new)
                .collect(Collectors.toList());
    }

}
