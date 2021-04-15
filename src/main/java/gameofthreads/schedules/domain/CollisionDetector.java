package gameofthreads.schedules.domain;

import gameofthreads.schedules.entity.Excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollisionDetector {
    List<Schedule> schedules;

    public CollisionDetector(Schedule schedule) {
        schedules = new ArrayList<>();
        schedules.add(schedule);
    }

    public void loadSchedules(List<Excel> excels) throws IOException {
        for (Excel excel : excels) {
            Optional<Schedule> newSchedule = new Parser(excel.getExcelName(), excel.getData()).parse();
            newSchedule.ifPresent(schedule -> schedules.add(schedule));
        }
    }

    public StringBuilder compareSchedules() {
        StringBuilder schedulesCollisions =
                new StringBuilder("{\"schedule\": \"" + schedules.get(0).getFileName() + "\"," +
                        "\"conflicts\": [");

        boolean firstConflict = true;
        StringBuilder response =
                new StringBuilder("");
        for (Schedule schedule : schedules) {
            boolean sameSchedule = schedule.equals(schedules.get(0));
            StringBuilder scheduleResponse =
                    new StringBuilder("{\"schedule\": \"" + schedule.getFileName() + "\"," +
                            "\"conflict meetings\": [");
            boolean result = schedule.compareSchedules(schedule, scheduleResponse, sameSchedule);
            if (!result) {
                if (!firstConflict)
                    scheduleResponse.append(",");
                scheduleResponse.append("]}");
                firstConflict = false;
                response.append(scheduleResponse);
            }

        }

        schedulesCollisions.append(response).append("]}");

        return schedulesCollisions;
    }
}
