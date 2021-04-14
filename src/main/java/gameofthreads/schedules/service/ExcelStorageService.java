package gameofthreads.schedules.service;

import gameofthreads.schedules.domain.CollisionDetector;
import gameofthreads.schedules.domain.Parser;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.repository.ExcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelStorageService {
    private final ExcelRepository excelRepository;
    private final StringBuilder collisions = new StringBuilder();

    public ExcelStorageService(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    private Optional<Schedule> checkCollisions(String fileName, Excel excel) throws IOException {
        Parser parser = new Parser(fileName, excel.getData());
        Optional<Schedule> optSchedule = parser.parse();
        List<Excel> excels = excelRepository.findAll();
        if (optSchedule.isPresent()) {
            CollisionDetector collisionDetector = new CollisionDetector(optSchedule.get());
            collisionDetector.loadSchedules(excels);
            collisions.append(collisionDetector.compareSchedules());
            return optSchedule;
        }
        return Optional.empty();
    }

    public Optional<List<Schedule>> saveFiles(MultipartFile[] files) {
        List<Schedule> schedules = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                Excel excel = new Excel(fileName, file.getContentType(), file.getBytes());
                Optional<Schedule> optSchedule = checkCollisions(fileName, excel);
                if (collisions.length() == 0 && optSchedule.isPresent() && excelRepository.findByExcelName(fileName).isEmpty()) {
                    schedules.add(optSchedule.get());
                    excelRepository.save(excel);
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        return Optional.of(schedules);
    }

    public Optional<Excel> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    public List<Excel> getFiles() {
        return excelRepository.findAll();
    }

    public StringBuilder getCollisions() {
        return collisions;
    }
}
