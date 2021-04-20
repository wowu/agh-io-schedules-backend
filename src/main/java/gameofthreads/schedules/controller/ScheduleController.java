package gameofthreads.schedules.controller;

import gameofthreads.schedules.dto.response.DetailedScheduleResponse;
import gameofthreads.schedules.dto.response.ScheduleListResponse;
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
import java.util.Optional;

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
        Optional<ScheduleListResponse> schedule = scheduleService.getAllSchedulesInJson((JwtAuthenticationToken) token);

        return schedule.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(schedule.get()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.GENERAL_ERROR.asJson());
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getSchedule(@PathVariable Integer scheduleId) {
        Optional<DetailedScheduleResponse> schedule = scheduleService.getScheduleInJson(scheduleId);

        return schedule.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(schedule.get()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.WRONG_SCHEDULE_ID.asJson());
    }

    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files) throws IOException {
        Pair<?, Boolean> upload = fileUploadService.saveFiles(files, scheduleService);

        return upload.getSecond() ?
                ResponseEntity.status(HttpStatus.OK).body(upload.getFirst()) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(upload.getFirst());
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