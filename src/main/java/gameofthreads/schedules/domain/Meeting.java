package gameofthreads.schedules.domain;

import gameofthreads.schedules.dto.response.UploadConflictResponse;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.MeetingFormat;
import gameofthreads.schedules.entity.MeetingType;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Meeting {
    private final Conference conference;
    private final LocalDateTime dateStart;
    private final LocalDateTime dateEnd;
    private final String subject;
    private final String group;
    private final String lecturerName;
    private final String lecturerSurname;
    private final MeetingType type;
    private final Integer lengthInHours;
    private final MeetingFormat format;
    private final String room;

    private Meeting(MeetingBuilder meetingBuilder) {
        this.conference = meetingBuilder.conference;
        this.dateStart = meetingBuilder.dateStart;
        this.dateEnd = meetingBuilder.dateEnd;
        this.subject = meetingBuilder.subject;
        this.group = meetingBuilder.group;
        this.lecturerName = meetingBuilder.lecturerName;
        this.lecturerSurname = meetingBuilder.lecturerSurname;
        this.type = meetingBuilder.type;
        this.lengthInHours = meetingBuilder.lengthInHours;
        this.format = meetingBuilder.format;
        this.room = meetingBuilder.room;
    }

    public Meeting(Conference conference, MeetingEntity meetingEntity) {
        this.conference = conference;
        this.dateStart = meetingEntity.getDateStart();
        this.dateEnd = meetingEntity.getDateEnd();
        this.subject = meetingEntity.getSubject();
        this.group = meetingEntity.getGroup();
        this.lecturerName = meetingEntity.getLecturerName();
        this.lecturerSurname = meetingEntity.getLecturerSurname();
        this.type = meetingEntity.getType();
        this.lengthInHours = meetingEntity.getLengthInHours();
        this.format = meetingEntity.getFormat();
        this.room = meetingEntity.getRoom();
    }

    /***
     * Two time periods P1 and P2 overlaps if, and only if, at least one of these conditions hold:
     *
     * P1 starts between the start and end of P2 (P2.from <= P1.from <= P2.to)
     * P2 starts between the start and end of P1 (P1.from <= P2.from <= P1.to)
     */
    public Pair<List<UploadConflictResponse.Conflict>, Boolean> compareMeeting(Meeting otherMeeting) {
        boolean overlap1 = !otherMeeting.getDateStart().isAfter(dateStart) && !dateStart.isAfter(otherMeeting.getDateEnd());
        boolean overlap2 = !dateEnd.isAfter(otherMeeting.getDateStart()) && !otherMeeting.getDateStart().isAfter(dateEnd);
        List<UploadConflictResponse.Conflict> conflicts = new ArrayList<>();
        if (overlap1 || overlap2) {
            CollisionReason lecturerOverlap = lecturerName.equals(otherMeeting.getLecturerName())
                    && lecturerSurname.equals(otherMeeting.getLecturerSurname()) ? CollisionReason.LECTURER : null;
            CollisionReason roomOverlap = !(format == MeetingFormat.HOME ||
                    otherMeeting.getFormat() == MeetingFormat.HOME) &&
                    room.equals(otherMeeting.getRoom()) ? CollisionReason.ROOM : null;
            CollisionReason groupOverlap = group.equals(otherMeeting.getGroup()) ? CollisionReason.GROUP : null;

            for (CollisionReason reason : new CollisionReason[]{lecturerOverlap, roomOverlap, groupOverlap}) {
                if (reason != null)
                    conflicts.add(new UploadConflictResponse
                            .Conflict(reason.toString(), new UploadConflictResponse.ConflictMeeting(this)));
            }

            if (conflicts.size() != 0) {
                return Pair.of(conflicts, Boolean.FALSE);
            }
        }

        return Pair.of(conflicts, Boolean.TRUE);
    }

    public Conference getConference() {
        return conference;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public String getSubject() {
        return subject;
    }

    public String getGroup() {
        return group;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getLecturerSurname() {
        return lecturerSurname;
    }

    public MeetingType getType() {
        return type;
    }

    public Integer getLengthInHours() {
        return lengthInHours;
    }

    public MeetingFormat getFormat() {
        return format;
    }

    public String getRoom() {
        return room;
    }

    public static final class MeetingBuilder {
        private Conference conference;
        private LocalDateTime dateStart;
        private LocalDateTime dateEnd;
        private String subject;
        private String group;
        private String lecturerName;
        private String lecturerSurname;
        private MeetingType type;
        private Integer lengthInHours;
        private MeetingFormat format;
        private String room;

        public MeetingBuilder conference(Conference conference) {
            this.conference = conference;
            return this;
        }

        public MeetingBuilder dateStart(LocalDateTime dateStart) {
            this.dateStart = dateStart;
            return this;
        }

        public MeetingBuilder dateEnd(LocalDateTime dateEnd) {
            this.dateEnd = dateEnd;
            return this;
        }

        public MeetingBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public MeetingBuilder group(String group) {
            this.group = group;
            return this;
        }

        public MeetingBuilder lecturerName(String lecturerName) {
            this.lecturerName = lecturerName;
            return this;
        }

        public MeetingBuilder lecturerSurname(String lecturerSurname) {
            this.lecturerSurname = lecturerSurname;
            return this;
        }

        public MeetingBuilder type(String type) {
            this.type = MeetingType.getTypeFromString(type);
            return this;
        }

        public MeetingBuilder lengthInHours(Integer lengthInHours) {
            this.lengthInHours = lengthInHours;
            return this;
        }

        public MeetingBuilder format(String format) {
            this.format = MeetingFormat.getFormatFromString(format);
            return this;
        }

        public MeetingBuilder room(String room) {
            this.room = room;
            return this;
        }

        public Meeting build() {
            return new Meeting(this);
        }

    }
}
