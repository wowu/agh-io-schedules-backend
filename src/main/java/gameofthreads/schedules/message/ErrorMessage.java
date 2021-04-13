package gameofthreads.schedules.message;

public enum ErrorMessage {
    WRONG_REFRESH_TOKEN("Wrong refresh token. Jwt payload does not contain the required data."),
    WRONG_CREDENTIALS("Wrong username or password."),
    WRONG_USERNAME("Wrong username."),
    WRONG_PASSWORD("Wrong password."),
    WRONG_TOKENS_SUBJECT("Wrong token's subject. User doesn't exist."),
    WRONG_EXCEL_FILE("Wrong file attached."),
    WRONG_DOWNLOAD_ID("Wrong excel file id.");

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
