package gameofthreads.schedules.entity;

public enum MeetingFormat {
    HOME("zdalnie"),
    UNIVERSITY("stacjonarnie");

    public String format;

    MeetingFormat(String format) {
        this.format = format;
    }

    public static MeetingFormat getFormatFromString(String format) {
        if (format.equals("zdalnie"))
            return HOME;

        return UNIVERSITY;
    }
}
