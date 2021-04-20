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
import gameofthreads.schedules.entity.UserEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.ScheduleRepository;
import gameofthreads.schedules.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    public Pair<?, Boolean> getAllSchedulesInJson(JwtAuthenticationToken jwtToken) {
        try {
            List<ScheduleEntity> scheduleEntities = scheduleRepository.findAll();
            Optional<UserEntity> user = userRepository.findById(Integer.parseInt((String) jwtToken.getTokenAttributes().get("userId")));

            if (user.isEmpty()) {
                return Pair.of(ErrorMessage.WRONG_USERNAME.asJson(), Boolean.FALSE);
            } else if (jwtToken.getTokenAttributes().get("scope").equals("ADMIN")) {
                return Pair.of(new ScheduleListResponse(scheduleEntities.stream()
                        .map(ShortScheduleResponse::new)
                        .collect(Collectors.toList())), Boolean.TRUE);
            } else {
                return Pair.of(new ScheduleListResponse(scheduleEntities.stream()
                        .filter(scheduleEntity -> scheduleEntity.getConferences().stream()
                                .flatMap(conferenceEntity -> conferenceEntity.getMeetingEntities().stream()
                                        .map(meetingEntity -> meetingEntity.getLecturerName() + " " + meetingEntity.getLecturerSurname()))
                                .collect(Collectors.toSet()).contains(user.get().getFirstname() + " " + user.get().getLastname()))
                        .map(ShortScheduleResponse::new)
                        .collect(Collectors.toList())), Boolean.TRUE);
            }
        } catch (Exception e) {
            return Pair.of(ErrorMessage.GENERAL_ERROR.asJson(), Boolean.FALSE);
        }
    }

    public Pair<?, Boolean> getScheduleInJson(Integer scheduleId, JwtAuthenticationToken jwtToken) {
        Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findById(scheduleId);
        Optional<UserEntity> user = userRepository.findById(Integer.parseInt((String) jwtToken.getTokenAttributes().get("userId")));

        if (scheduleEntity.isEmpty())
            return Pair.of(ErrorMessage.WRONG_SCHEDULE_ID.asJson(), Boolean.FALSE);
        else if (user.isEmpty())
            return Pair.of(ErrorMessage.WRONG_USERNAME.asJson(), Boolean.FALSE);
        else if (jwtToken.getTokenAttributes().get("scope").equals("LECTURER") &&
                !scheduleEntity.get().getConferences().stream()
                        .flatMap(conferenceEntity -> conferenceEntity.getMeetingEntities().stream()
                                .map(meetingEntity -> meetingEntity.getLecturerName() + " " + meetingEntity.getLecturerSurname()))
                        .collect(Collectors.toSet()).contains(user.get().getFirstname() + " " + user.get().getLastname())) {
            return Pair.of(ErrorMessage.INSUFFICIENT_SCOPE.asJson(), Boolean.FALSE);
        }

        return Pair.of(new DetailedScheduleResponse(scheduleEntity.get()), Boolean.TRUE);
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
