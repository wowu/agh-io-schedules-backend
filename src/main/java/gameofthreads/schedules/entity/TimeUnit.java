package gameofthreads.schedules.entity;

import java.util.Map;

public enum TimeUnit {
    MINUTE, HOUR, DAY;

    public static TimeUnit getType(String name) {
        Map<String, TimeUnit> map = Map.of(
                "minute", MINUTE,
                "hour", HOUR,
                "day", DAY
        );

        return map.get(name);
    }

    public int getMinutes() {
        if (this == TimeUnit.DAY) return 1440;
        if (this == TimeUnit.HOUR) return 60;
        return 1;
    }

}
