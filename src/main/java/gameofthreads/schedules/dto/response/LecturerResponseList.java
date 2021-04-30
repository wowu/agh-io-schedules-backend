package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.List;

public class LecturerResponseList implements Serializable {
    final List<LecturerResponse> lecturers;

    public LecturerResponseList(List<LecturerResponse> lecturers) {
        this.lecturers = lecturers;
    }

}
