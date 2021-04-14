package gameofthreads.schedules.domain;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

@SuppressWarnings("all")
public class Parser {
    private final String filePath;
    private final byte[] data;

    private Conference conference;
    private double conferenceNumber;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private String times;
    private String subject;
    private String group;
    private String lecturer;
    private String type;
    private double lengthInHours;
    private String format;
    private String room;

    public Parser(String filePath, byte[] data) {
        this.filePath = filePath;
        this.data = data;
        this.conferenceNumber = -1;
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        if (dateToConvert == null)
            return null;

        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Optional<Schedule> parse() throws FileNotFoundException {
        Schedule schedule = new Schedule(filePath);
        try {
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(data));
            Sheet firstSheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row nextRow : firstSheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                Iterator<Cell> cellIterator = nextRow.cellIterator();


                double lastConference = conferenceNumber;
                double tmpConference = cellIterator.next().getNumericCellValue();
                conferenceNumber = tmpConference == 0 ? conferenceNumber : tmpConference;
                if (lastConference != conferenceNumber) {
                    if (lastConference != -1) {
                        schedule.getConferences().add(conference);
                    }
                    conference = new Conference(schedule);
                }

                dateStart = Optional.ofNullable
                        (convertToLocalDateTimeViaInstant(cellIterator.next().getDateCellValue())).orElse(dateStart);
                String tmpTimes = cellIterator.next().getStringCellValue();
                times = tmpTimes.equals("") ? "0.00-23.59" : tmpTimes;

                subject = cellIterator.next().getStringCellValue();
                group = cellIterator.next().getStringCellValue();
                lecturer = cellIterator.next().getStringCellValue();
                type = cellIterator.next().getStringCellValue();
                lengthInHours = cellIterator.next().getNumericCellValue();
                format = cellIterator.next().getStringCellValue();
                room = cellIterator.next().getStringCellValue();

                LocalDateTime meetingStartTime = dateStart
                        .withHour(Integer.parseInt(times.split("-")[0].split("\\.")[0]))
                        .withMinute(Integer.parseInt(times.split("-")[0].split("\\.")[1]));

                LocalDateTime meetingEndTime = dateStart
                        .withHour(Integer.parseInt(times.split("-")[1].split("\\.")[0]))
                        .withMinute(Integer.parseInt(times.split("-")[1].split("\\.")[1]));

                Meeting newMeeting = new Meeting.MeetingBuilder()
                        .conference(conference)
                        .dateStart(meetingStartTime)
                        .dateEnd(meetingEndTime)
                        .subject(subject)
                        .group(group)
                        .lecturer(lecturer)
                        .type(type)
                        .lengthInHours((int) lengthInHours)
                        .format(format)
                        .room(room)
                        .build();

                conference.getMeetings().add(newMeeting);

            }

        } catch (IllegalStateException ignored) {
            schedule.getConferences().add(conference);
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.of(schedule);
    }
}
