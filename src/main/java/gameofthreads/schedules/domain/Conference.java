package gameofthreads.schedules.domain;

import gameofthreads.schedules.dto.response.UploadConflictResponse;
import gameofthreads.schedules.entity.ConferenceEntity;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Conference {
    private final Schedule schedule;
    private final Set<Meeting> meetings;

    public Conference(Schedule schedule) {
        this.schedule = schedule;
        this.meetings = new HashSet<>();
    }

    public Conference(Schedule schedule, ConferenceEntity conferenceEntity) {
        this.schedule = schedule;
        this.meetings = conferenceEntity.getMeetingEntities().stream().
                map(meetingEntity -> new Meeting(this, meetingEntity))
                .collect(Collectors.toSet());
    }

    public Pair<List<UploadConflictResponse.Conflict>, Boolean> compareConference(Meeting meeting, Conference otherConference, boolean sameSchedule) {
        boolean noCollisions = true;
        List<UploadConflictResponse.Conflict> conflicts = new ArrayList<>();
        for (Meeting otherMeeting : otherConference.getMeetings()) {
            if (!sameSchedule || !meeting.equals(otherMeeting)) {
                Pair<List<UploadConflictResponse.Conflict>, Boolean> noCollisionsMeeting = meeting.compareMeeting(otherMeeting);
                if (!noCollisionsMeeting.getSecond()) {
                    conflicts.addAll(noCollisionsMeeting.getFirst());
                    noCollisions = false;
                }
            }
        }
        return Pair.of(conflicts, noCollisions);
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Set<Meeting> getMeetings() {
        return meetings;
    }

}
