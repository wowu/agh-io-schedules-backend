package gameofthreads.schedules.service;

import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.repository.ExcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ExcelStorageService {
    @Autowired
    private ExcelRepository excelRepository;

    public Optional<Excel> saveFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            if (excelRepository.findByExcelName(fileName).isPresent())
                return excelRepository.findByExcelName(fileName);
            Excel excel = new Excel(fileName, file.getContentType(), file.getBytes());
            return Optional.of(excelRepository.save(excel));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Excel> getFile(Integer fileId) {
        return excelRepository.findById(fileId);
    }

    public List<Excel> getFiles() {
        return excelRepository.findAll();
    }
}
