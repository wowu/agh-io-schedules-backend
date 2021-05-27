package gameofthreads.schedules.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import gameofthreads.schedules.dto.response.NotificationResponse;

import java.io.Serializable;
import java.util.List;

public class AddMyNotificationsRequest implements Serializable {
    @JsonProperty("default")
    public final boolean global;
    public final List<NotificationResponse> notifications;

    public AddMyNotificationsRequest(boolean global, List<NotificationResponse> notifications) {
        this.global = global;
        this.notifications = notifications;
    }
}
