package gameofthreads.schedules.controller;

import com.google.gson.Gson;
import gameofthreads.schedules.domain.Schedule;
import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.service.ExcelStorageService;
import gameofthreads.schedules.service.ScheduleService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/schedule")
public class ExcelController {
    private final ExcelStorageService excelStorageService;
    private final ScheduleService scheduleService;

    public ExcelController(ExcelStorageService excelStorageService, ScheduleService scheduleService) {
        this.excelStorageService = excelStorageService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/getFiles")
    public ResponseEntity<?> getAllFiles(Model model) {
        List<Excel> excels = excelStorageService.getFiles();
        model.addAttribute("excels", excels);


        return ResponseEntity.status(HttpStatus.OK).body
                (new Gson().toJson(excels.stream().map(Excel::getExcelName).collect(Collectors.toList())));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files) {
        StringBuilder collisions = new StringBuilder();
        Optional<List<Schedule>> schedules = excelStorageService.saveFiles(files, collisions);
        schedules.ifPresent(scheduleList -> scheduleList.forEach(scheduleService::saveScheduleToDatabase));

        if (schedules.isEmpty() || !(collisions.length() == 0))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(collisions);

        return ResponseEntity.status(HttpStatus.OK).body("Files uploaded successfully.");
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer fileId) {
        if (excelStorageService.getFile(fileId).isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.WRONG_DOWNLOAD_ID.asJson());
        Excel excel = excelStorageService.getFile(fileId).get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(excel.getExcelType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + excel.getExcelName() + "\"")
                .body(new ByteArrayResource(excel.getData()));
    }
}