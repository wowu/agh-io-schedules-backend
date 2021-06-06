package gameofthreads.schedules.util;

import gameofthreads.schedules.notification.model.Conference;
import gameofthreads.schedules.notification.model.Meeting;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class HtmlCreator {
    private static String tdPack(String value) {
        return "<td>" + value + "</td>";
    }

    private static String createGreetings(String fullName) {
        return "<h3>Cześć " + fullName + " !</h3>" +
                "<p>" +
                "Jak Ci mija dzień? Mam nadzieję, że jest super! " +
                "Pamiętasz o zbliżającej się konferencji? " +
                "Jeśli nie, to nie musisz się martwić. " +
                "Poniżej znajduje się lista wydarzeń, które mogą Cię zainteresować." +
                "<p>";
    }

    private static String createCssStyle() {
        return "<style>\n" +
                "#customers {\n" +
                "  font-family: Arial, Helvetica, sans-serif;\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "#customers td, #customers th {\n" +
                "  border: 1px solid #ddd;\n" +
                "  padding: 8px;\n" +
                "}\n" +
                "\n" +
                "#customers tr:nth-child(even){background-color: #f2f2f2;}\n" +
                "\n" +
                "#customers tr:hover {background-color: #ddd;}\n" +
                "\n" +
                "#customers th {\n" +
                "  padding-top: 12px;\n" +
                "  padding-bottom: 12px;\n" +
                "  text-align: left;\n" +
                "  background-color: #4CAF50;\n" +
                "  color: white;\n" +
                "}\n" +
                "</style>";
    }

    private static String createMeetingsTable(TreeSet<Meeting> meetings) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<table id=\"customers\">");
        stringBuilder.append("<tr>");

        Stream.of(
                "<th>Data</th>",
                "<th>Początek</th>",
                "<th>Koniec</th>",
                "<th>Przedmiot</th>",
                "<th>Grupa</th>",
                "<th>Rodzaj</th>",
                "<th>Prowadzący</th>",
                "<th>Sala</th>"
        ).forEach(stringBuilder::append);

        stringBuilder.append("</tr>");

        for (Meeting meeting : meetings) {
            stringBuilder.append("<tr>");
            stringBuilder.append(tdPack(meeting.dateStart.format(DateTimeFormatter.ISO_LOCAL_DATE)));
            stringBuilder.append(tdPack(meeting.dateStart.format(DateTimeFormatter.ISO_LOCAL_TIME)));
            stringBuilder.append(tdPack(meeting.dateEnd.format(DateTimeFormatter.ISO_LOCAL_TIME)));
            stringBuilder.append(tdPack(meeting.subject));
            stringBuilder.append(tdPack(meeting.group));
            stringBuilder.append(tdPack(meeting.type.getPolishTranslation()));
            stringBuilder.append(tdPack(meeting.getFullName()));
            stringBuilder.append(tdPack(meeting.room.equals("") ? "Zdalnie" : meeting.room));
            stringBuilder.append("</tr>");
        }

        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    public static String createMeetingsEmail(TreeSet<Meeting> meetings) {
        return createCssStyle() +
                createGreetings(meetings.first().getFullName()) +
                createMeetingsTable(meetings);
    }

    public static String createConferencesEmail(Set<Conference> conferences) {
        int counter = 1;
        final String emptyName = "";
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(createCssStyle());
        stringBuilder.append(createGreetings(emptyName));

        for (Conference conference : conferences) {
            stringBuilder.append("<h3>Konferencja : ").append(counter).append("</h3>");
            stringBuilder.append(createMeetingsTable(conference.getMeetings()));
            counter++;
        }

        return stringBuilder.toString();
    }

}
