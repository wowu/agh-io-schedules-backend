package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class LecturerResponseList implements Serializable {
    public final List<LecturerMediumResponse> lecturers;

    public LecturerResponseList(List<LecturerMediumResponse> lecturers) {
        this.lecturers = lecturers;
    }

}
