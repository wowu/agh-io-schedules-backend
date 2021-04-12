package gameofthreads.schedules.message;

public enum ErrorMessage {
    WRONG_TOKEN("Wrong token."),
    WRONG_CREDENTIALS("Wrong username or password."),
    WRONG_USERNAME("Wrong username."),
    WRONG_PASSWORD("Wrong password."),
    WRONG_TOKENS_SUBJECT("Wrong token's subject. User doesn't exist.");

    private String text;

    ErrorMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return "ERROR: " + text;
    }

    public String asJson(){
        return "{ \"ERROR\": \"" + text + "\" }";
    }

}
