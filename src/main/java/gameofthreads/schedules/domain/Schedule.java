package gameofthreads.schedules.domain;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private final String fileName;
    private final List<Conference> conferences;

    public Schedule(String fileName) {
        this.fileName = fileName;
        this.conferences = new ArrayList<>();
    }

    /**
     * returns true if schedules have no collisions
     */
    public boolean compareSchedules(Schedule otherSchedule, StringBuilder result, boolean sameSchedule) {
        boolean noCollisions = true;
        for (Conference conference : conferences) {
            for (Conference otherConference : otherSchedule.getConferences()) {
                if (!conference.compareConference(otherConference, result, sameSchedule))
                    noCollisions = false;
            }
        }

        return noCollisions;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Conference> getConferences() {
        return conferences;
    }
}
