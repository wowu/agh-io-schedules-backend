package gameofthreads.schedules.dto.request;

import java.io.Serializable;
import java.util.List;

public class AddSubscriptionRequest implements Serializable {
    public final int conferenceId;
    public final List<String> emailList;

    public AddSubscriptionRequest(int conferenceId, List<String> emailList) {
        this.conferenceId = conferenceId;
        this.emailList = emailList;
    }

}
