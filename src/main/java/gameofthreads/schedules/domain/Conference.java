package gameofthreads.schedules.domain;

import gameofthreads.schedules.entity.ConferenceEntity;

import java.util.*;
import java.util.stream.Collectors;

public class Conference {
    private final Schedule schedule;
    private final List<Meeting> meetings;

    public Conference(Schedule schedule) {
        this.schedule = schedule;
        this.meetings = new ArrayList<>();
    }

    public Conference(Schedule schedule, ConferenceEntity conferenceEntity) {
        this.schedule = schedule;
        this.meetings = conferenceEntity.getMeetingEntities().stream().
                map(meetingEntity -> new Meeting(this, meetingEntity))
                .collect(Collectors.toList());
    }


    /**
     * returns true if conferences have no collisions
     */
    public boolean compareConference(Conference otherConference, StringBuilder result, boolean sameSchedule) {
        boolean noCollisions = true;
        for (Meeting meeting : meetings) {
            StringBuilder response = new StringBuilder();
            for (Meeting otherMeeting : otherConference.getMeetings()) {
                if (!sameSchedule ||
                        !(meetings.indexOf(meeting) >= otherConference.getMeetings().indexOf(otherMeeting))) {
                    boolean noCollisionsMeeting = meeting.compareMeeting(otherMeeting, response);
                    if (!noCollisions && !noCollisionsMeeting)
                        response.append(",");
                    if (!noCollisionsMeeting) {
                        result.append(response);
                        noCollisions = false;
                    }


                }
            }
        }
        return noCollisions;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

}
