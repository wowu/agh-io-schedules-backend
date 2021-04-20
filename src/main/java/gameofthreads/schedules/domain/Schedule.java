package gameofthreads.schedules.domain;

import gameofthreads.schedules.dto.response.UploadConflictResponse;
import gameofthreads.schedules.entity.ExcelEntity;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Schedule {
    private final static Set<String> publicLinkSet = new HashSet<>();

    private final String fileName;
    private final String publicLink;
    private final Set<Conference> conferences;
    private ExcelEntity excelEntity;

    public Schedule(String fileName) {
        this.fileName = fileName.split("\\.")[0];
        this.publicLink = generatePublicLink();
        this.conferences = new HashSet<>();
    }

    public static void loadPublicLinks(Set<String> publicLinks) {
        publicLinkSet.addAll(publicLinks);
    }

    private String generatePublicLink() {
        final int length = 64;
        String publicLink = RandomString.make(length);

        while (publicLinkSet.contains(publicLink)) {
            publicLink = RandomString.make(length);
        }

        publicLinkSet.add(publicLink);
        return publicLink;
    }

    /**
     * returns true if schedules have no collisions
     */
    public Pair<UploadConflictResponse.ConflictList, Boolean> compareEventWithSchedule(Meeting meeting, Schedule otherSchedule, boolean sameSchedule) {
        boolean noCollisions = true;
        UploadConflictResponse.ConflictList conflictList = new UploadConflictResponse.ConflictList(otherSchedule.fileName);
        for (Conference otherConference : otherSchedule.getConferences()) {
            Pair<List<UploadConflictResponse.Conflict>, Boolean> compareConference =
                    meeting.getConference().compareConference(meeting, otherConference, sameSchedule);
            if (!compareConference.getSecond()) {
                conflictList.conflictedEvents.addAll(compareConference.getFirst());
                noCollisions = false;
            }
        }

        return Pair.of(conflictList, noCollisions);
    }

    public String getFileName() {
        return fileName;
    }

    public Set<Conference> getConferences() {
        return conferences;
    }

    public String getPublicLink() {
        return publicLink;
    }

    public ExcelEntity getExcelEntity() {
        return excelEntity;
    }

    public void setExcelEntity(ExcelEntity excelEntity) {
        this.excelEntity = excelEntity;
    }
}
