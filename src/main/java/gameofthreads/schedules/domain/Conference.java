package gameofthreads.schedules.domain;

import java.util.*;

public class Conference {
    private final Schedule schedule;
    private final List<Meeting> meetings;

    Conference(Schedule schedule) {
        this.schedule = schedule;
        this.meetings = new ArrayList<>();
    }

    /**
     * returns true if conferences have no collisions
     */
    public boolean compareConference(Conference otherConference, StringBuilder result, boolean sameSchedule) {
        boolean noCollisions = true;
        for (Meeting meeting : meetings) {
            boolean noCollisionsMeeting = true;
            StringBuilder response =
                    new StringBuilder(String.format("\n%17s %s", "-", meeting.toString()));
            for (Meeting otherMeeting : otherConference.getMeetings()) {
                if (!sameSchedule || !meeting.equals(otherMeeting)) {
                    if (!meeting.compareMeeting(otherMeeting, response)) {
                        noCollisionsMeeting = false;
                    }
                }
            }
            if (!noCollisionsMeeting) {
                result.append(response);
                noCollisions = false;
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
