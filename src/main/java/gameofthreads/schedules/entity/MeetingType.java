package gameofthreads.schedules.entity;

public enum MeetingType {
    LECTURE("W"),
    LABORATORIES("L"),
    CLASSES("C");

    public String type;

    MeetingType(String type) {
        this.type = type;
    }

    public static MeetingType getTypeFromString(String type) {
        if (type.equals("W"))
            return LECTURE;
        else if (type.equals("L"))
            return LABORATORIES;
        else
            return CLASSES;
    }

}
