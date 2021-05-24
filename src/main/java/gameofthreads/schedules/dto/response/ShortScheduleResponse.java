package gameofthreads.schedules.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gameofthreads.schedules.entity.ScheduleEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShortScheduleResponse implements Serializable {
    public final Integer id;
    public final String name;
    public final String description;
    public final Integer eventCount;
    public final LocalDateTime firstEventDate;
    public final LocalDateTime lastEventDate;
    public final Boolean notifications;
    @JsonIgnore
    public final List<MeetingResponse> events;

    public ShortScheduleResponse(ScheduleEntity scheduleEntity) {
        this.id = scheduleEntity.getId();
        this.name = scheduleEntity.getFileName();
        this.events = scheduleEntity.getConferences().stream().
                flatMap(conferenceEntity -> conferenceEntity.getMeetingEntities().stream()).
                map(MeetingResponse::new)
                .collect(Collectors.toList());
        this.description = scheduleEntity.getDescription();
        this.eventCount = events.size();
        this.notifications = scheduleEntity.getNotifications();
        this.firstEventDate = Collections
                .min(events.stream().map(meetingResponse -> meetingResponse.beginTime).collect(Collectors.toList()));
        this.lastEventDate = Collections
                .max(events.stream().map(meetingResponse -> meetingResponse.endTime).collect(Collectors.toList()));
    }
}
