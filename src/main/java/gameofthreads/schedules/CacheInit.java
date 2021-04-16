package gameofthreads.schedules;

import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.service.ScheduleService;
import org.springframework.stereotype.Component;

@Component
public class CacheInit {
    private final ScheduleService scheduleService;

    public CacheInit(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        this.loadPublicLinks();
    }

    public void loadPublicLinks(){
        Schedule.loadPublicLinks(scheduleService.findPublicLinks());
    }

}
