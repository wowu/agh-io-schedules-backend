package gameofthreads.schedules.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_REGEX = Pattern
            .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmailList(List<String> emails){
        long correctEmail =  emails.stream()
                .filter(Validator::validateEmail)
                .count();

        return correctEmail == emails.size() - 1;
    }

    public static boolean validateEmail(String email){
        Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.find();
    }

}
