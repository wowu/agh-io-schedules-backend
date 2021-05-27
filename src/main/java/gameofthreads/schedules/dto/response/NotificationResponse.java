package gameofthreads.schedules.dto.response;

import java.io.Serializable;

public class NotificationResponse implements Serializable {
    public final Integer value;
    public final String unit;

    public NotificationResponse(Integer value, String unit) {
        this.value = value;
        this.unit = unit;
    }
}
