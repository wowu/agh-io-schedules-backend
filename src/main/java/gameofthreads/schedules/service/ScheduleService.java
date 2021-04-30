package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.Conference;
import gameofthreads.schedules.domain.Meeting;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.dto.response.DetailedScheduleResponse;
import gameofthreads.schedules.dto.response.ScheduleListResponse;
import gameofthreads.schedules.dto.response.ShortScheduleResponse;
import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.LecturerEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.*;
import org.springframework.data.util.Pair;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ConferenceRepository conferenceRepository;
    private final MeetingRepository meetingRepository;
    private final ExcelRepository excelRepository;
    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ConferenceRepository conferenceRepository, MeetingRepository meetingRepository, ExcelRepository excelRepository, LecturerRepository lecturerRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.conferenceRepository = conferenceRepository;
        this.meetingRepository = meetingRepository;
        this.excelRepository = excelRepository;
        this.lecturerRepository = lecturerRepository;
        this.userRepository = userRepository;
    }

    private boolean isUserARole(JwtAuthenticationToken jwtToken, String role) {
        return jwtToken.getTokenAttributes().get("scope").equals(role);
    }

    public Pair<?, Boolean> getAllSchedulesInJson(JwtAuthenticationToken jwtToken) {
        try {
            Set<ScheduleEntity> scheduleEntities = scheduleRepository.fetchAllWithConferencesAndMeetings();
            Optional<LecturerEntity> lecturerEntity = lecturerRepository.findByEmail_Email((String) jwtToken.getTokenAttributes().get("sub"));

            if (isUserARole(jwtToken, "ADMIN")) {
                return Pair.of(new ScheduleListResponse(scheduleEntities.stream()
                        .map(ShortScheduleResponse::new)
                        .collect(Collectors.toList())), Boolean.TRUE);
            }

            if (lecturerEntity.isEmpty()) {
                return Pair.of(ErrorMessage.WRONG_USERNAME.asJson(), Boolean.FALSE);
            }

            return Pair.of(new ScheduleListResponse(scheduleEntities.stream()
                    .filter(scheduleEntity -> scheduleEntity.getConferences().stream()
                            .flatMap(conferenceEntity -> conferenceEntity.getMeetingEntities().stream()
                                    .map(MeetingEntity::getFullName))
                            .collect(Collectors.toSet()).contains(lecturerEntity.get().getFullName()))
                    .map(ShortScheduleResponse::new)
                    .collect(Collectors.toList())), Boolean.TRUE);

        } catch (Exception e) {
            return Pair.of(ErrorMessage.GENERAL_ERROR.asJson(), Boolean.FALSE);
        }
    }

    public Pair<?, Boolean> getScheduleInJson(Integer scheduleId, JwtAuthenticationToken jwtToken) {
        Optional<ScheduleEntity> scheduleEntity = scheduleRepository.fetchWithConferencesAndMeetings(scheduleId);
        Optional<LecturerEntity> lecturerEntity = lecturerRepository.findByEmail_Email((String) jwtToken.getTokenAttributes().get("sub"));

        if (scheduleEntity.isEmpty()) {
            return Pair.of(ErrorMessage.WRONG_SCHEDULE_ID.asJson(), Boolean.FALSE);
        }

        if (isUserARole(jwtToken, "ADMIN")) {
            return Pair.of(new DetailedScheduleResponse(scheduleEntity.get()), Boolean.TRUE);
        }

        if (lecturerEntity.isEmpty())
            return Pair.of(ErrorMessage.WRONG_USERNAME.asJson(), Boolean.FALSE);
        if (isUserARole(jwtToken, "LECTURER") &&
                !scheduleEntity.get().getConferences().stream()
                        .flatMap(conferenceEntity -> conferenceEntity.getMeetingEntities().stream()
                                .map(MeetingEntity::getFullName))
                        .collect(Collectors.toSet()).contains(lecturerEntity.get().getFullName())) {
            return Pair.of(ErrorMessage.INSUFFICIENT_SCOPE.asJson(), Boolean.FALSE);
        }

        return Pair.of(new DetailedScheduleResponse(scheduleEntity.get()), Boolean.TRUE);
    }

    public Pair<?, Boolean> getScheduleInJson(String uuid) {
        Optional<ScheduleEntity> scheduleEntity = scheduleRepository.fetchWithConferencesAndMeetingsByUuid(uuid);

        if (scheduleEntity.isEmpty())
            return Pair.of(ErrorMessage.WRONG_UUID.asJson(), Boolean.FALSE);

        return Pair.of(new DetailedScheduleResponse(scheduleEntity.get()), Boolean.TRUE);
    }

    @Transactional
    public Pair<?, Boolean> modifySchedule(Integer scheduleId, String name, String description) {
        try {
            if (name != null && description != null)
                scheduleRepository.updateAllMetadata(scheduleId, name, description);
            else if (name != null)
                scheduleRepository.updateFilenameMetadata(scheduleId, name);
            else if (description != null)
                scheduleRepository.updateDescriptionMetadata(scheduleId, description);

            return Pair.of("", Boolean.TRUE);
        } catch (Exception e) {
            return Pair.of(ErrorMessage.GENERAL_ERROR.asJson(), Boolean.FALSE);
        }
    }

    @Transactional
    public Pair<?, Boolean> deleteSchedule(Integer scheduleId) {
        long scheduleCount = scheduleRepository.count();
        long conferenceCount = conferenceRepository.count();
        long meetingCount = meetingRepository.count();
        long excelCount = excelRepository.count();

        Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findById(scheduleId);
        if (scheduleEntity.isEmpty())
            return Pair.of("", Boolean.FALSE);
        long conferencesToDelete = conferenceRepository.countConferenceEntitiesBySchedule(scheduleEntity.get());
        List<ConferenceEntity> conferenceEntities = conferenceRepository.findAllBySchedule(scheduleEntity.get());
        long meetingsToDelete = conferenceEntities.stream()
                .map(meetingRepository::countMeetingEntitiesByConference)
                .reduce(0L, Long::sum);

        scheduleRepository.deleteById(scheduleId);

        if (scheduleCount == scheduleRepository.count() + 1 &&
                conferenceCount == conferenceRepository.count() + conferencesToDelete &&
                meetingCount == meetingRepository.count() + meetingsToDelete &&
                excelCount == excelRepository.count() + 1) {
            return Pair.of("", Boolean.TRUE);
        }

        return Pair.of("", Boolean.FALSE);
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
