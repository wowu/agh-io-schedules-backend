package gameofthreads.schedules.entity;

import java.util.Map;

public enum MeetingType {
    LECTURE("W"),
    LABORATORIES("L"),
    CLASSES("C");

    public String type;

    private static final Map<MeetingType, String> polishTranslation = Map.of(
            LECTURE, "Wykład",
            LABORATORIES, "Laboratoria",
            CLASSES, "Ćwiczenia"
    );

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

    public String getPolishTranslation() {
        return polishTranslation.get(this);
    }

}
