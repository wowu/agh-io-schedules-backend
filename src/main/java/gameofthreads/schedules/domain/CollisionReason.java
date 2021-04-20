package gameofthreads.schedules.domain;

public enum CollisionReason {
    ROOM("Room is engaged."),
    LECTURER("Lecturer has another event."),
    GROUP("Group has another event.");

    private final String reason;

    CollisionReason(String reason) {
        this.reason = reason;
    }

    public String toString() {
        return this.reason;
    }
}
