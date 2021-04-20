package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class UploadSuccessfulResponse implements Serializable {
    public final List<DetailedScheduleResponse> uploaded;

    public UploadSuccessfulResponse(List<DetailedScheduleResponse> uploaded) {
        this.uploaded = uploaded;
    }
}
