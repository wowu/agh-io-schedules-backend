package gameofthreads.schedules.controller;

import gameofthreads.schedules.entity.ExcelEntity;
import gameofthreads.schedules.message.ErrorMessage;
import gameofthreads.schedules.service.FileUploadService;
import gameofthreads.schedules.service.ScheduleService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("api/schedules")
public class ScheduleController {
    private final FileUploadService fileUploadService;
    private final ScheduleService scheduleService;

    public ScheduleController(FileUploadService fileUploadService, ScheduleService scheduleService) {
        this.fileUploadService = fileUploadService;
        this.scheduleService = scheduleService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllSchedules(Authentication token) {
        Pair<?, Boolean> schedules = scheduleService.getAllSchedulesInJson((JwtAuthenticationToken) token);

        return schedules.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(schedules.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schedules.getFirst());
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getSchedule(@PathVariable Integer scheduleId, Authentication token) {
        Pair<?, Boolean> schedule = scheduleService.getScheduleInJson(scheduleId, (JwtAuthenticationToken) token);

        return schedule.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(schedule.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schedule.getFirst());
    }

    @PutMapping(value = "/{scheduleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modifyScheduleMetadata(@PathVariable Integer scheduleId,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "description", required = false) String description,
                                                    @RequestParam(value = "notifications", required = false) Boolean notifications) {
        Pair<?, Boolean> schedule = scheduleService.modifySchedule(scheduleId, name, description, notifications);

        return schedule.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(schedule.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schedule.getFirst());
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer scheduleId) {
        Pair<?, Boolean> schedule = scheduleService.deleteSchedule(scheduleId);

        return schedule.getSecond() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schedule.getFirst());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("files[]") MultipartFile[] files) throws IOException {
        Pair<?, Boolean> upload = fileUploadService.saveFiles(files, scheduleService);

        return upload.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(upload.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(upload.getFirst());
    }

    @PostMapping(value = "/{scheduleId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> changeSchedule(@PathVariable Integer scheduleId, @RequestParam("file") MultipartFile file) throws IOException {
        Pair<?, Boolean> update = fileUploadService.updateSchedule(file, scheduleId, scheduleService);

        return update.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(update.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(update.getFirst());
    }

    @GetMapping("/{scheduleId}/file")
    public ResponseEntity<?> downloadFile(@PathVariable Integer scheduleId) {
        if (fileUploadService.getFile(scheduleId).isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.WRONG_DOWNLOAD_ID.asJson());
        ExcelEntity excelEntity = fileUploadService.getFile(scheduleId).get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(excelEntity.getExcelType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + excelEntity.getExcelName() + "\"")
                .body(new ByteArrayResource(excelEntity.getData()));
    }

}