package gameofthreads.schedules.domain;

import gameofthreads.schedules.entity.ScheduleEntity;
import net.bytebuddy.utility.RandomString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Schedule {
    private final static Set<String> publicLinkSet = new HashSet<>();

    private final String fileName;
    private final String publicLink;
    private final List<Conference> conferences;

    public Schedule(String fileName) {
        this.fileName = fileName;
        this.publicLink = generatePublicLink();
        this.conferences = new ArrayList<>();
    }

    public Schedule(ScheduleEntity scheduleEntity) {
        this.fileName = scheduleEntity.getFileName();
        this.publicLink = scheduleEntity.getPublicLink();
        this.conferences = scheduleEntity.getConferences().stream().
                map(conferenceEntity -> new Conference(this, conferenceEntity))
                .collect(Collectors.toList());
    }

    private String generatePublicLink(){
        final int length = 64;
        String publicLink = RandomString.make(length);

        while(publicLinkSet.contains(publicLink)){
            publicLink = RandomString.make(length);
        }

        publicLinkSet.add(publicLink);
        return publicLink;
    }

    public static void loadPublicLinks(Set<String> publicLinks){
        publicLinkSet.addAll(publicLinks);
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

    public String getPublicLink() {
        return publicLink;
    }

}
