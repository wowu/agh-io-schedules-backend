package gameofthreads.schedules.domain;

import gameofthreads.schedules.dto.response.UploadConflictResponse;
import gameofthreads.schedules.entity.ExcelEntity;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollisionDetector {
    List<Schedule> schedules;

    public CollisionDetector(Schedule schedule) {
        schedules = new ArrayList<>();
        schedules.add(schedule);
    }

    public void loadSchedules(List<ExcelEntity> excelEntities) throws IOException {
        for (ExcelEntity excelEntity : excelEntities) {
            Optional<Schedule> newSchedule = new Parser(excelEntity.getExcelName(), excelEntity.getData()).parse();
            newSchedule.ifPresent(schedule -> schedules.add(schedule));
        }
    }

    public Pair<UploadConflictResponse.ConflictSchedule, Boolean> compareSchedules() {
        Schedule thisSchedule = schedules.get(0);
        List<Meeting> thisMeetings = thisSchedule.getConferences().stream().
                flatMap(conference -> conference.getMeetings().stream())
                .collect(Collectors.toList());
        UploadConflictResponse.ConflictSchedule conflictSchedule =
                new UploadConflictResponse.ConflictSchedule(thisSchedule.getFileName());
        boolean noCollisions = true;

        for (Meeting meeting : thisMeetings) {
            List<UploadConflictResponse.ConflictList> conflictLists = new ArrayList<>();
            boolean noCollisionsEvent = true;
            for (Schedule schedule : schedules) {
                boolean sameSchedule = schedule.equals(thisSchedule);
                Pair<UploadConflictResponse.ConflictList, Boolean> compare =
                        thisSchedule.compareEventWithSchedule(meeting, schedule, sameSchedule);

                if (!compare.getSecond()) {
                    noCollisionsEvent = false;
                    conflictLists.add(compare.getFirst());
                }
            }
            if (!noCollisionsEvent) {
                noCollisions = false;
                conflictSchedule.eventsWithConflicts.add(new UploadConflictResponse
                        .ConflictEvents(new UploadConflictResponse.ConflictMeeting(meeting), conflictLists));
            }
        }

        return Pair.of(conflictSchedule, noCollisions);
    }
}
