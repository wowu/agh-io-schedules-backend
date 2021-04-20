package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.MeetingFormat;
import gameofthreads.schedules.entity.MeetingType;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MeetingResponse implements Serializable {
    public final Integer id;
    public final LocalDateTime beginTime;
    public final LocalDateTime endTime;
    public final String eventName;
    public final String groupName;
    public final String lecturerName;
    public final String lecturerSurname;
    public final MeetingType type;
    public final Integer hours;
    public final MeetingFormat form;
    public final String room;

    public MeetingResponse(MeetingEntity meetingEntity) {
        this.id = meetingEntity.getId();
        this.beginTime = meetingEntity.getDateStart();
        this.endTime = meetingEntity.getDateEnd();
        this.eventName = meetingEntity.getSubject();
        this.groupName = meetingEntity.getGroup();
        this.lecturerName = meetingEntity.getLecturerName();
        this.lecturerSurname = meetingEntity.getLecturerSurname();
        this.type = meetingEntity.getType();
        this.hours = meetingEntity.getLengthInHours();
        this.form = meetingEntity.getFormat();
        this.room = meetingEntity.getRoom();
    }
}
