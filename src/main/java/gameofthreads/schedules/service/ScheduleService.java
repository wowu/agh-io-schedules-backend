package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.Conference;
import gameofthreads.schedules.domain.Meeting;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public String getSchedule(ScheduleEntity scheduleEntity) {
        Schedule schedule = new Schedule(scheduleEntity);
        StringBuilder scheduleJson = new StringBuilder("{\"schedule\": \"" + schedule.getFileName()
                + "\"," + "\"meetings\": [");
        boolean firstConflict = true;

        for (Conference conference : schedule.getConferences()) {
            for (Meeting meeting : conference.getMeetings()) {
                if (!firstConflict)
                    scheduleJson.append(",");
                firstConflict = false;
                scheduleJson.append(meeting.asJson());
            }
        }

        scheduleJson.append("]}");

        return scheduleJson.toString();
    }

    public Optional<String> getAllSchedulesInJson() {
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findAll();
        StringBuilder allSchedulesJson = new StringBuilder("{\"schedules\": [");
        boolean firstSchedule = true;

        try {
            for (ScheduleEntity scheduleEntity : scheduleEntities) {
                if (!firstSchedule)
                    allSchedulesJson.append(",");
                firstSchedule = false;
                allSchedulesJson.append(getSchedule(scheduleEntity));
            }
        } catch (Exception e) {
            return Optional.empty();
        }

        allSchedulesJson.append("]}");

        return Optional.of(allSchedulesJson.toString());
    }

    public Optional<String> getScheduleInJson(Integer scheduleId) {
        Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findById(scheduleId);
        if (scheduleEntity.isEmpty())
            return Optional.empty();

        return Optional.of(getSchedule(scheduleEntity.get()));
    }


    public ScheduleEntity getScheduleEntity(Schedule schedule) {
        ScheduleEntity scheduleEntity = new ScheduleEntity(schedule.getFileName(), schedule.getPublicLink());
        for (Conference conference : schedule.getConferences()) {
            addConferenceToSchedule(conference, scheduleEntity);
        }
        return scheduleEntity;
    }

    public void addConferenceToSchedule(Conference conference, ScheduleEntity scheduleEntity) {
        ConferenceEntity conferenceEntity = new ConferenceEntity(scheduleEntity);
        for (Meeting meeting : conference.getMeetings()) {
            addMeetingToConference(meeting, conferenceEntity);
        }
        scheduleEntity.getConferences().add(conferenceEntity);
    }

    public void addMeetingToConference(Meeting meeting, ConferenceEntity conferenceEntity) {
        MeetingEntity meetingEntity = new MeetingEntity(conferenceEntity, meeting.getDateStart(),
                meeting.getDateEnd(), meeting.getSubject(), meeting.getGroup(), meeting.getLecturer(),
                meeting.getType(), meeting.getLengthInHours(), meeting.getFormat(), meeting.getRoom());

        conferenceEntity.getMeetingEntities().add(meetingEntity);
    }

    public Set<String> findPublicLinks() {
        return scheduleRepository.findAll()
                .stream()
                .map(ScheduleEntity::getPublicLink)
                .collect(Collectors.toSet());
    }

}
