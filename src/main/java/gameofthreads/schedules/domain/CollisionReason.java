package gameofthreads.schedules.domain;

public enum CollisionReason {
    ROOM("Sala"),
    LECTURER("Prowadzący"),
    GROUP("Grupa");

    private final String reason;

    CollisionReason(String reason) {
        this.reason = reason;
    }

    public String toString() {
        return this.reason;
    }
}
