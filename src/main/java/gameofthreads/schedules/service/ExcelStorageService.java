package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.CollisionDetector;
import gameofthreads.schedules.domain.Parser;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.repository.ExcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelStorageService {
    private final ExcelRepository excelRepository;
    private StringBuilder collisions = new StringBuilder();
    private final List<Excel> approvedExcels = new ArrayList<>();

    public ExcelStorageService(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    public Optional<Schedule> checkCollisions(String fileName, Excel excel) throws IOException {
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
    public Optional<List<Schedule>> saveFiles(MultipartFile[] files, StringBuilder resultCollisions) {
        List<Schedule> schedules = new ArrayList<>();
        List<Excel> excels = new ArrayList<>();
        HashSet<String> existingExcels = new HashSet<>(excelRepository.findAllExcelNames());

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                Excel excel = new Excel(fileName, file.getContentType(), file.getBytes());
                Optional<Schedule> optSchedule = checkCollisions(fileName, excel);
                resultCollisions.append(collisions);
                if (collisions.length() == 0 && optSchedule.isPresent() && !existingExcels.contains(fileName)) {
                    schedules.add(optSchedule.get());
                    approvedExcels.add(excel);
                    excels.add(excel);
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        excelRepository.saveAll(excels);

        return Optional.of(schedules);
    }

    public Optional<Excel> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    public List<Excel> getFiles() {
        return excelRepository.findAll();
    }

}
