package gameofthreads.schedules.notification.model;

import gameofthreads.schedules.entity.TimeUnit;

public final class ScheduleDetails {
    private final TimeUnit timeUnit;
    private final Integer timeValue;
    final boolean fullNotifications;

    public ScheduleDetails(TimeUnit timeUnit, Integer timeValue, boolean fullNotifications) {
        this.timeUnit = timeUnit;
        this.timeValue = timeValue;
        this.fullNotifications = fullNotifications;
    }

    public Integer timeInMinute() {
        return timeValue * timeUnit.getMinutes();
    }

}
