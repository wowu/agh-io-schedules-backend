package gameofthreads.schedules.dto.request;

import java.io.Serializable;
import java.util.List;

public class AddSubscriptionRequest implements Serializable {
    public final int scheduleId;
    public final List<String> emailList;

    public AddSubscriptionRequest(int scheduleId, List<String> emailList) {
        this.scheduleId = scheduleId;
        this.emailList = emailList;
    }

}
