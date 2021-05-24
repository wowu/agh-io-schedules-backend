package gameofthreads.schedules.message;

public enum ErrorMessage {
    WRONG_REFRESH_TOKEN("Niepoprawny token odświeżania. Payload Jwt nie zawiera wymaganych danych."),
    WRONG_CREDENTIALS("Niepoprawna nazwa użytkownika lub hasło."),
    WRONG_USERNAME("Niepoprawna nazwa użytkownika."),
    WRONG_PASSWORD("Niepoprawne hasło."),
    WRONG_TOKENS_SUBJECT("Niepoprawny token. Użytkownik nie istnieje."),
    WRONG_DOWNLOAD_ID("Niepoprawny identyfikator pliku excel."),
    WRONG_CONFERENCE_ID("Niepoprawny identyfikator konferencji. Konferencja nie istnieje."),
    WRONG_SUBSCRIPTION_ID("Niepoprawny identyfikator subskrypcji. Subskrypcja nie istnieje."),
    INCORRECT_EMAIL("Niepoprawny adres email."),
    WRONG_SCHEDULE_ID("Niepoprawny identyfikator harmonogramu. Harmonogram nie istnieje."),
    WRONG_UUID("Niepoprawny UUID."),
    INSUFFICIENT_SCOPE("To żądanie wymaga większych uprawnień."),
    NO_FILES("Nie dołączono żadnych plików."),
    GENERAL_ERROR("Coś poszło nie tak. Przepraszamy!"),
    NOT_AVAILABLE_EMAIL("Ten adres email nie jest dostępny."),
    WRONG_LECTURER_ID("Niepoprawny identyfikator wykładowcy. Wykładowca nie istnieje."),
    WRONG_USER_ID("Niepoprawny identyfikator użytkownika. Użytkownik nie istnieje."),
    NO_LECTURER_WITH_EMAIL("Nie ma wykładowcy o takim adresie email."),
    WRONG_CONFERENCE_UUID("Niepoprawny UUID konferencji. Konferencja nie istnieje."),
    EXISTING_SUBSCRIPTION("Nie ma subskrypcji z takim adresem email dla tego harmonogramu."),
    NO_USER_WITH_EMAIL("Nie ma wykładowcy/użytkownika o takim adresie email."),
    FORBIDDEN_USER("Wyświetlanie subskrypcji innych użytkowników jest niedozwolone.");

    private final String text;

    ErrorMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return "error: " + text;
    }

    public String asJson() {
        return "{ \"error\": \"" + text + "\" }";
    }

}
