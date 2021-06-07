package gameofthreads.schedules.notification.model;

import gameofthreads.schedules.entity.TimeUnit;

public final class ScheduleDetails {
    private final TimeUnit timeUnit;
    private final Integer timeValue;
    final boolean fullNotifications;
    private final String fullName;

    public ScheduleDetails(TimeUnit timeUnit, Integer timeValue, boolean fullNotifications, String fullName) {
        this.timeUnit = timeUnit;
        this.timeValue = timeValue;
        this.fullNotifications = fullNotifications;
        this.fullName = fullName;
    }

    public Integer timeInMinute() {
        return timeValue * timeUnit.getMinutes();
    }

    public String getFullName() {
        return fullName;
    }

}
