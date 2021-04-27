package gameofthreads.schedules.entity;

public enum Role {
    ADMIN("ADMIN"),
    LECTURER("LECTURER");

    public String name;

    Role(String name) {
        this.name = name;
    }

}
