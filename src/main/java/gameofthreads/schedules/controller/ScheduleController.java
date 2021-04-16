package gameofthreads.schedules.controller;

import com.google.gson.Gson;
import gameofthreads.schedules.entity.Excel;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.service.FileUploadService;
import gameofthreads.schedules.service.ScheduleService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/schedule")
public class ScheduleController {
    private final FileUploadService fileUploadService;
    private final ScheduleService scheduleService;

    public ScheduleController(FileUploadService fileUploadService, ScheduleService scheduleService) {
        this.fileUploadService = fileUploadService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/getSchedules")
    public ResponseEntity<?> getAllSchedules() {
        Optional<String> schedules = scheduleService.getAllSchedulesInJson();

        return schedules.
                map(s -> ResponseEntity.status(HttpStatus.OK).body(s))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.GENERAL_ERROR.asJson()));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getSchedule(@RequestParam Integer scheduleId) {
        Optional<String> schedule = scheduleService.getScheduleInJson(scheduleId);

        return schedule.
                map(s -> ResponseEntity.status(HttpStatus.OK).body(s))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.WRONG_SCHEDULE_ID.asJson()));
    }

    @GetMapping("/getFiles")
    public ResponseEntity<?> getAllFiles() {
        List<Excel> excels = fileUploadService.getFiles();

        return ResponseEntity.status(HttpStatus.OK).body
                (new Gson().toJson(excels.stream().map(Excel::getExcelName).collect(Collectors.toList())));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files) {
        Optional<StringBuilder> collisions = fileUploadService.saveFiles(files, scheduleService);

        if (collisions.isPresent() && !(collisions.get().length() == 0))
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(collisions.get());

        return ResponseEntity.status(HttpStatus.OK).body("{\"message\" : \"Files uploaded successfully.\"}");
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer fileId) {
        if (fileUploadService.getFile(fileId).isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.WRONG_DOWNLOAD_ID.asJson());
        Excel excel = fileUploadService.getFile(fileId).get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(excel.getExcelType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + excel.getExcelName() + "\"")
                .body(new ByteArrayResource(excel.getData()));
    }
}