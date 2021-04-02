package gameofthreads.schedules.entity;

public enum Role {
    ADMIN("ADMIN"),
    LECTURER("LECTURER"),
    STUDENT("STUDENT");

    public String name;

    Role(String name) {
        this.name = name;
    }

}
