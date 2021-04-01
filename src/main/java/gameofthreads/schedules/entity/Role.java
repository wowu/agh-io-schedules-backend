package gameofthreads.schedules.entity;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    LECTURER("ROLE_LECTURER"),
    STUDENT("ROLE_STUDENT"),
    ANONYMOUS("ROLE_ANONYMOUS");

    public String name;

    Role(String name) {
        this.name = name;
    }

}
