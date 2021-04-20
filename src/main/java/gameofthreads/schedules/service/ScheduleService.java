package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.Conference;
import gameofthreads.schedules.domain.Meeting;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.dto.response.DetailedScheduleResponse;
import gameofthreads.schedules.dto.response.ScheduleListResponse;
import gameofthreads.schedules.dto.response.ShortScheduleResponse;
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

    public Optional<ScheduleListResponse> getAllSchedulesInJson() {
        try {
            List<ScheduleEntity> scheduleEntities = scheduleRepository.findAll();
            return Optional.of(new ScheduleListResponse(scheduleEntities.stream()
                    .map(ShortScheduleResponse::new)
                    .collect(Collectors.toList())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<DetailedScheduleResponse> getScheduleInJson(Integer scheduleId) {
        Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findById(scheduleId);
        if (scheduleEntity.isEmpty())
            return Optional.empty();

        return Optional.of(new DetailedScheduleResponse(scheduleEntity.get()));
    }


    public ScheduleEntity getScheduleEntity(Schedule schedule) {
        ScheduleEntity scheduleEntity =
                new ScheduleEntity(schedule.getFileName(), schedule.getPublicLink(), schedule.getExcelEntity());
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
                meeting.getDateEnd(), meeting.getSubject(), meeting.getGroup(), meeting.getLecturerName(),
                meeting.getLecturerSurname(), meeting.getType(), meeting.getLengthInHours(),
                meeting.getFormat(), meeting.getRoom());

        conferenceEntity.getMeetingEntities().add(meetingEntity);
    }

    public Set<String> findPublicLinks() {
        return scheduleRepository.findAll()
                .stream()
                .map(ScheduleEntity::getPublicLink)
                .collect(Collectors.toSet());
    }

}
