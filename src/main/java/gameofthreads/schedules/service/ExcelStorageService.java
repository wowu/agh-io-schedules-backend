package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.CollisionDetector;
import gameofthreads.schedules.domain.Parser;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.repository.ExcelRepository;
import gameofthreads.schedules.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExcelStorageService {
    private final ExcelRepository excelRepository;
    private final ScheduleRepository scheduleRepository;
    private List<Excel> approvedExcels = new ArrayList<>();
    private StringBuilder collisions = new StringBuilder();

    public ExcelStorageService(ExcelRepository excelRepository, ScheduleRepository scheduleRepository) {
        this.excelRepository = excelRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public Optional<Schedule> checkCollisions(String fileName, Excel excel) throws IOException {
        if (!fileName.contains(".xlsx") && !fileName.contains(".xls"))
            return Optional.empty();
        Parser parser = new Parser(fileName, excel.getData());
        Optional<Schedule> optSchedule = parser.parse();
        List<Excel> excels = excelRepository.findAll();
        if (optSchedule.isPresent()) {
            CollisionDetector collisionDetector = new CollisionDetector(optSchedule.get());
            collisionDetector.loadSchedules(excels);
            collisionDetector.loadSchedules(approvedExcels);
            collisions = collisionDetector.compareSchedules();
            return optSchedule;
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<StringBuilder> saveFiles(MultipartFile[] files, ScheduleStorageService scheduleStorageService) {
        StringBuilder resultCollisions = new StringBuilder("{\"schedules\": [");
        approvedExcels = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();
        List<Excel> excels = new ArrayList<>();
        HashSet<String> existingExcels = new HashSet<>(excelRepository.findAllExcelNames());

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                Excel excel = new Excel(fileName, file.getContentType(), file.getBytes());
                Optional<Schedule> optSchedule = checkCollisions(fileName, excel);
                if (!file.equals(files[0]))
                    resultCollisions.append(",");
                resultCollisions.append(collisions);
                if (collisions.toString().contains("[]") && optSchedule.isPresent() && !existingExcels.contains(fileName)) {
                    schedules.add(optSchedule.get());
                    approvedExcels.add(excel);
                    excels.add(excel);
                }
                if (optSchedule.isEmpty())
                    return Optional.of(new StringBuilder(ErrorMessage.WRONG_EXCEL_FILE.asJson()));
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        List<ScheduleEntity> scheduleEntities =
                schedules.stream().map(scheduleStorageService::getScheduleEntity).collect(Collectors.toList());
        excelRepository.saveAll(excels);
        scheduleRepository.saveAll(scheduleEntities);
        resultCollisions.append("]}");

        if (schedules.size() == files.length)
            return Optional.empty();

        return Optional.of(resultCollisions);
    }

    public Optional<Excel> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    public List<Excel> getFiles() {
        return excelRepository.findAll();
    }

}
