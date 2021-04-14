package gameofthreads.schedules.domain;

import gameofthreads.schedules.entity.Excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollisionDetector {
    final static String dashes = "--------------------";
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
        StringBuilder invalidSchedules = new StringBuilder();
        boolean noCollisions = true;

        StringBuilder response =
                new StringBuilder("\n" + dashes + " " + schedules.get(0).getFileName() + " " + dashes);
        for (Schedule schedule : schedules) {
            boolean sameSchedule = schedule.equals(schedules.get(0));
            StringBuilder scheduleResponse = new StringBuilder(String.format("\n%8s", ""))
                    .append(dashes).append(" ")
                    .append(schedule.getFileName()).append(" ").append(dashes);
            boolean result = schedule.compareSchedules(schedule, scheduleResponse, sameSchedule);
            if (!result) {
                noCollisions = false;
                response.append(scheduleResponse);
            }

        }

        if (!noCollisions)
            invalidSchedules.append(response);

        return invalidSchedules;
    }
}
