package gameofthreads.schedules.util;

import gameofthreads.schedules.notification.model.Meeting;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class HtmlCreator {
    private static String tdPack(String value) {
        return "<td>" + value + "</td>";
    }

    private static String createGreetings() {
        return "<h3>Cześć!</h3>" +
                "<p>Oto wydarzenie w harmonogramie, który śledzisz.<p>";
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

    public static String createContext(Meeting meeting) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(createCssStyle());
        stringBuilder.append(createGreetings());
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

        stringBuilder.append("<tr>");
        stringBuilder.append(tdPack(meeting.getDateStart().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        stringBuilder.append(tdPack(meeting.getDateStart().format(DateTimeFormatter.ISO_LOCAL_TIME)));
        stringBuilder.append(tdPack(meeting.dateEnd.format(DateTimeFormatter.ISO_LOCAL_TIME)));
        stringBuilder.append(tdPack(meeting.subject));
        stringBuilder.append(tdPack(meeting.group));
        stringBuilder.append(tdPack(meeting.type.getPolishTranslation()));
        stringBuilder.append(tdPack(meeting.getFullName()));
        stringBuilder.append(tdPack(meeting.room.equals("") ? "Zdalnie" : meeting.room));
        stringBuilder.append("</tr>");

        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

}
