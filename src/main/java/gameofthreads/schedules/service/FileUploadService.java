package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.CollisionDetector;
import gameofthreads.schedules.domain.Parser;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.dto.response.DetailedScheduleResponse;
import gameofthreads.schedules.dto.response.UploadConflictResponse;
import gameofthreads.schedules.dto.response.UploadSuccessfulResponse;
import gameofthreads.schedules.entity.*;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.ConferenceRepository;
import gameofthreads.schedules.repository.ExcelRepository;
import gameofthreads.schedules.repository.LecturerRepository;
import gameofthreads.schedules.repository.ScheduleRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileUploadService {
    private final ExcelRepository excelRepository;
    private final ConferenceRepository conferenceRepository;
    private final ScheduleRepository scheduleRepository;
    private final LecturerRepository lecturerRepository;
    private List<ExcelEntity> approvedExcelEntities = new ArrayList<>();

    public FileUploadService(ExcelRepository excelRepository, ConferenceRepository conferenceRepository, ScheduleRepository scheduleRepository, LecturerRepository lecturerRepository) {
        this.excelRepository = excelRepository;
        this.conferenceRepository = conferenceRepository;
        this.scheduleRepository = scheduleRepository;
        this.lecturerRepository = lecturerRepository;
    }

    private Set<LecturerEntity> getLecturers(ScheduleEntity scheduleEntity, List<LecturerEntity> lecturersInDB) {
        Set<LecturerEntity> lecturers = new HashSet<>();
        for (ConferenceEntity conference : scheduleEntity.getConferences()) {
            for (MeetingEntity meeting : conference.getMeetingEntities()) {
                LecturerEntity lecturer = new LecturerEntity(meeting.getLecturerName(), meeting.getLecturerSurname());
                if (!lecturersInDB.contains(lecturer))
                    lecturers.add(lecturer);
            }
        }
        return lecturers;
    }

    public CollisionResponse checkCollisions(String fileName, ExcelEntity excelEntity, Integer updateScheduleId) throws IOException {
        if (!fileName.contains(".xlsx") && !fileName.contains(".xls"))
            return new CollisionResponse(null, null, ErrorMessage.GENERAL_ERROR, Boolean.FALSE);
        Parser parser = new Parser(fileName, excelEntity.getData());
        Optional<Schedule> optSchedule = parser.parse();
        List<ExcelEntity> excelEntities = (updateScheduleId == null) ?
                excelRepository.findAll() :
                excelRepository.findAllWithoutId(updateScheduleId);
        if (optSchedule.isPresent()) {
            CollisionDetector collisionDetector = new CollisionDetector(optSchedule.get());
            collisionDetector.loadSchedules(excelEntities);
            collisionDetector.loadSchedules(approvedExcelEntities);
            Pair<UploadConflictResponse.ConflictSchedule, Boolean> compareSchedules = collisionDetector.compareSchedules();
            return new CollisionResponse(optSchedule.get(), compareSchedules.getFirst(), null, compareSchedules.getSecond());
        }

        return new CollisionResponse(null, null, ErrorMessage.GENERAL_ERROR, Boolean.FALSE);
    }

    @Transactional
    public Pair<?, Boolean> saveFiles(MultipartFile[] files, ScheduleService scheduleService) throws IOException {
        if (files.length == 1 && Objects.equals(files[0].getOriginalFilename(), ""))
            return Pair.of(ErrorMessage.NO_FILES.asJson(), Boolean.FALSE);
        approvedExcelEntities = new ArrayList<>();
        List<ScheduleEntity> schedules = new ArrayList<>();
        List<UploadConflictResponse.ConflictSchedule> schedulesWithConflicts = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            ExcelEntity excelEntity = new ExcelEntity(fileName, file.getContentType(), file.getBytes());
            CollisionResponse collisionResponse = checkCollisions(Objects.requireNonNull(fileName), excelEntity, null);
            if (collisionResponse.noCollisions) {
                collisionResponse.schedule.setExcelEntity(excelEntity);
                ScheduleEntity scheduleEntity = scheduleService.getScheduleEntity(collisionResponse.schedule);
                excelEntity.setSchedule(scheduleEntity);
                schedules.add(scheduleEntity);
                approvedExcelEntities.add(excelEntity);
            } else {
                schedulesWithConflicts.add(collisionResponse.conflictSchedule);
            }
        }

        if (schedulesWithConflicts.size() > 0) {
            return Pair.of(new UploadConflictResponse(schedulesWithConflicts), Boolean.FALSE);
        }

        List<LecturerEntity> lecturersInDB = lecturerRepository.findAll();
        List<LecturerEntity> lecturers = new ArrayList<>();
        for (ScheduleEntity schedule : schedules) {
            lecturers.addAll(getLecturers(schedule, lecturersInDB));
        }

        lecturerRepository.saveAll(lecturers);
        scheduleRepository.saveAll(schedules);

        return Pair.of(new UploadSuccessfulResponse(schedules.stream()
                .map(DetailedScheduleResponse::new).collect(Collectors.toList())), Boolean.TRUE);
    }

    @Transactional
    public Pair<?, Boolean> updateSchedule(MultipartFile file, Integer scheduleId, ScheduleService scheduleService) throws IOException {
        Optional<ScheduleEntity> schedule = scheduleRepository.findById(scheduleId);
        if (schedule.isEmpty())
            return Pair.of(ErrorMessage.WRONG_SCHEDULE_ID.asJson(), Boolean.FALSE);

        String fileName = file.getOriginalFilename();
        approvedExcelEntities = new ArrayList<>();

        ExcelEntity excelEntity = new ExcelEntity(fileName, file.getContentType(), file.getBytes());
        CollisionResponse collisionResponse = checkCollisions(Objects.requireNonNull(fileName), excelEntity, scheduleId);
        if (collisionResponse.noCollisions) {
            ScheduleEntity newScheduleEntity = scheduleService.getScheduleEntity(collisionResponse.schedule);
            ScheduleEntity oldScheduleEntity = schedule.get();
            conferenceRepository.deleteAll(oldScheduleEntity.getConferences());
            oldScheduleEntity.setConferenceEntities(newScheduleEntity.getConferences());
            newScheduleEntity.getConferences().forEach(conferenceEntity -> conferenceEntity.setSchedule(oldScheduleEntity));
            oldScheduleEntity.getExcelEntity().setExcelName(excelEntity.getExcelName());
            oldScheduleEntity.getExcelEntity().setData(excelEntity.getData());

            lecturerRepository.saveAll(getLecturers(oldScheduleEntity, lecturerRepository.findAll()));
            scheduleRepository.save(oldScheduleEntity);
            return Pair.of(new DetailedScheduleResponse(oldScheduleEntity), Boolean.TRUE);
        } else {
            return Pair.of(collisionResponse.conflictSchedule, Boolean.FALSE);
        }
    }

    public Optional<ExcelEntity> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    private static class CollisionResponse {
        public final Schedule schedule;
        public final UploadConflictResponse.ConflictSchedule conflictSchedule;
        public final ErrorMessage errorMessage;
        public final Boolean noCollisions;

        private CollisionResponse(Schedule schedule, UploadConflictResponse.ConflictSchedule conflictSchedule, ErrorMessage errorMessage, Boolean noCollisions) {
            this.schedule = schedule;
            this.conflictSchedule = conflictSchedule;
            this.errorMessage = errorMessage;
            this.noCollisions = noCollisions;
        }
    }

}
