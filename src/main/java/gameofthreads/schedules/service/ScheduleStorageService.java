package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.Conference;
import gameofthreads.schedules.domain.Meeting;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.repository.ConferenceRepository;
import gameofthreads.schedules.repository.MeetingRepository;
import gameofthreads.schedules.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class ScheduleStorageService {
    private final ScheduleRepository scheduleRepository;
    private final ConferenceRepository conferenceRepository;
    private final MeetingRepository meetingRepository;

    public ScheduleStorageService(ScheduleRepository scheduleRepository, ConferenceRepository conferenceRepository, MeetingRepository meetingRepository) {
        this.scheduleRepository = scheduleRepository;
        this.conferenceRepository = conferenceRepository;
        this.meetingRepository = meetingRepository;
    }

    public void saveScheduleToDatabase(Schedule schedule) {
        ScheduleEntity scheduleEntity = new ScheduleEntity(schedule.getFileName());
        scheduleRepository.save(scheduleEntity);
        for (Conference conference : schedule.getConferences()) {
            saveConferenceToDatabase(conference, scheduleEntity);
        }
    }

    private void saveConferenceToDatabase(Conference conference, ScheduleEntity scheduleEntity) {
        ConferenceEntity conferenceEntity = new ConferenceEntity(scheduleEntity);
        conferenceRepository.save(conferenceEntity);
        for (Meeting meeting : conference.getMeetings()) {
            saveMeetingToDatabase(meeting, conferenceEntity);
        }
    }

    private void saveMeetingToDatabase(Meeting meeting, ConferenceEntity conferenceEntity) {
        MeetingEntity meetingEntity = new MeetingEntity(conferenceEntity, meeting.getDateStart(),
                meeting.getDateEnd(), meeting.getSubject(), meeting.getGroup(), meeting.getLecturer(),
                meeting.getType(), meeting.getLengthInHours(), meeting.getFormat(), meeting.getRoom());

        meetingRepository.save(meetingEntity);
    }
}
