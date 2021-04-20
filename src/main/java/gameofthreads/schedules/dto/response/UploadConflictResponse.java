package gameofthreads.schedules.dto.response;

import gameofthreads.schedules.domain.Meeting;
import gameofthreads.schedules.entity.MeetingFormat;
import gameofthreads.schedules.entity.MeetingType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UploadConflictResponse implements Serializable {
    public final List<ConflictSchedule> schedulesWithConflicts;

    public UploadConflictResponse(List<ConflictSchedule> schedulesWithConflicts) {
        this.schedulesWithConflicts = schedulesWithConflicts;
    }

    public static class ConflictSchedule implements Serializable {
        public final String scheduleName;
        public final List<ConflictEvents> eventsWithConflicts;

        public ConflictSchedule(String scheduleName) {
            this.scheduleName = scheduleName;
            this.eventsWithConflicts = new ArrayList<>();
        }
    }

    public static class ConflictEvents implements Serializable {
        public final ConflictMeeting event;
        public final List<ConflictList> conflicts;

        public ConflictEvents(ConflictMeeting event, List<ConflictList> conflicts) {
            this.event = event;
            this.conflicts = conflicts;
        }
    }

    public static class ConflictList implements Serializable {
        public final String conflictedScheduleName;
        public final List<Conflict> conflictedEvents;

        public ConflictList(String conflictedScheduleName) {
            this.conflictedScheduleName = conflictedScheduleName;
            this.conflictedEvents = new ArrayList<>();
        }
    }

    public static class Conflict implements Serializable {
        public final String reason;
        public final ConflictMeeting event;

        public Conflict(String reason, ConflictMeeting event) {
            this.reason = reason;
            this.event = event;
        }
    }

    public static class ConflictMeeting implements Serializable {
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

        public ConflictMeeting(Meeting meeting) {
            this.beginTime = meeting.getDateStart();
            this.endTime = meeting.getDateEnd();
            this.eventName = meeting.getSubject();
            this.groupName = meeting.getGroup();
            this.lecturerName = meeting.getLecturerName();
            this.lecturerSurname = meeting.getLecturerSurname();
            this.type = meeting.getType();
            this.hours = meeting.getLengthInHours();
            this.form = meeting.getFormat();
            this.room = meeting.getRoom();
        }
    }

}
