package gameofthreads.schedules.message;

public enum ErrorMessage {
    WRONG_REFRESH_TOKEN("Wrong refresh token. Jwt payload does not contain the required data."),
    WRONG_CREDENTIALS("Wrong username or password."),
    WRONG_USERNAME("Wrong username."),
    WRONG_PASSWORD("Wrong password."),
    WRONG_TOKENS_SUBJECT("Wrong token's subject. User doesn't exist."),
    WRONG_EXCEL_FILE("One of the attached files is wrong."),
    WRONG_DOWNLOAD_ID("Wrong excel file id."),
    WRONG_CONFERENCE_ID("Wrong conference id. Conference doesn't exist."),
    INCORRECT_EMAIL_LIST("Emails list contains incorrect email address");

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
