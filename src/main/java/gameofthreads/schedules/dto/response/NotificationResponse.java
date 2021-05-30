package gameofthreads.schedules.dto.response;

import java.io.Serializable;
import java.util.Objects;

public class NotificationResponse implements Serializable {
    public final Integer value;
    public final String unit;

    public NotificationResponse(Integer value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationResponse that = (NotificationResponse) o;
        return Objects.equals(value, that.value) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }

}
