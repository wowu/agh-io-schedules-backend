package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class GetSubscribers implements Serializable {
    public final List<AddSubscription> subscribers;

    public GetSubscribers(List<AddSubscription> subscribers) {
        this.subscribers = subscribers;
    }

}
