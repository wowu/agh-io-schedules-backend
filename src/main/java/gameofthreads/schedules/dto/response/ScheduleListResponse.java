package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class ScheduleListResponse implements Serializable {
    public final List<ShortScheduleResponse> schedules;

    public ScheduleListResponse(List<ShortScheduleResponse> schedules) {
        this.schedules = schedules;
    }
}
