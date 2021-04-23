package gameofthreads.schedules.message;

public enum ErrorMessage {
    WRONG_REFRESH_TOKEN("Wrong refresh token. Jwt payload does not contain the required data."),
    WRONG_CREDENTIALS("Wrong username or password."),
    WRONG_USERNAME("Wrong username."),
    WRONG_PASSWORD("Wrong password."),
    WRONG_TOKENS_SUBJECT("Wrong token's subject. User doesn't exist."),
    WRONG_DOWNLOAD_ID("Wrong excel file id."),
    WRONG_CONFERENCE_ID("Wrong conference id. Conference doesn't exist."),
    WRONG_SUBSCRIPTION_ID("Wrong subscription id. Subscription doesn't exist."),
    INCORRECT_EMAIL("Incorrect email."),
    WRONG_SCHEDULE_ID("Wrong schedule id. Schedule doesn't exist."),
    WRONG_UUID("Wrong uuid."),
    INSUFFICIENT_SCOPE("This request requires higher privileges."),
    NO_FILES("You have not attached any files."),
    GENERAL_ERROR("Something did go wrong. Sorry for that!");

    private final String text;

    ErrorMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return "ERROR: " + text;
    }

    public String asJson() {
        return "{ \"ERROR\": \"" + text + "\" }";
    }

}
