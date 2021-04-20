package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.CollisionDetector;
import gameofthreads.schedules.domain.Parser;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.dto.response.DetailedScheduleResponse;
import gameofthreads.schedules.dto.response.UploadConflictResponse;
import gameofthreads.schedules.dto.response.UploadSuccessfulResponse;
import gameofthreads.schedules.entity.ExcelEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.ExcelRepository;
import gameofthreads.schedules.repository.ScheduleRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileUploadService {
    private final ExcelRepository excelRepository;
    private final ScheduleRepository scheduleRepository;
    private List<ExcelEntity> approvedExcelEntities = new ArrayList<>();

    public FileUploadService(ExcelRepository excelRepository, ScheduleRepository scheduleRepository) {
        this.excelRepository = excelRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public CollisionResponse checkCollisions(String fileName, ExcelEntity excelEntity) throws IOException {
        if (!fileName.contains(".xlsx") && !fileName.contains(".xls"))
            return new CollisionResponse(null, null, ErrorMessage.GENERAL_ERROR, Boolean.FALSE);
        Parser parser = new Parser(fileName, excelEntity.getData());
        Optional<Schedule> optSchedule = parser.parse();
        List<ExcelEntity> excelEntities = excelRepository.findAll();
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
            CollisionResponse collisionResponse = checkCollisions(Objects.requireNonNull(fileName), excelEntity);
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

        scheduleRepository.saveAll(schedules);

        return Pair.of(new UploadSuccessfulResponse(schedules.stream()
                .map(DetailedScheduleResponse::new).collect(Collectors.toList())), Boolean.TRUE);
    }

    public Optional<ExcelEntity> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    public List<ExcelEntity> getFiles() {
        return excelRepository.findAll();
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
