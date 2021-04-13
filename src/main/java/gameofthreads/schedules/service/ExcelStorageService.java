package gameofthreads.schedules.service;

import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.repository.ExcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ExcelStorageService {
    private final ExcelRepository excelRepository;

    public ExcelStorageService(ExcelRepository excelRepository) {
        this.excelRepository = excelRepository;
    }

    public Optional<Excel> saveFiles(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            try {
                if (excelRepository.findByExcelName(fileName).isEmpty()) {
                    Excel excel = new Excel(fileName, file.getContentType(), file.getBytes());
                    excelRepository.save(excel);
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        return Optional.of(new Excel());
    }

    public Optional<Excel> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    public List<Excel> getFiles() {
        return excelRepository.findAll();
    }
}
