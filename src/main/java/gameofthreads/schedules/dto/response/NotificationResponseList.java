package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class NotificationResponseList implements Serializable {
    public List<NotificationResponse> notifications;

    public NotificationResponseList(List<NotificationResponse> notifications) {
        this.notifications = notifications;
    }

    public NotificationResponseList() { }

}
