package gameofthreads.schedules.domain;

public enum CollisionReason {
    ROOM("Sala jest zajęta w tym czasie."),
    LECTURER("Prowadzący ma inne zajęcia w tym czasie."),
    GROUP("Grupa ma inne zajęcia w tym czasie.");

    private final String reason;

    CollisionReason(String reason) {
        this.reason = reason;
    }

    public String toString() {
        return this.reason;
    }
}
