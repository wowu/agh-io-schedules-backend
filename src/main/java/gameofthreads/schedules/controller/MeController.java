package gameofthreads.schedules.controller;

import gameofthreads.schedules.service.LecturerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/me")
public class MeController {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);
    private final LecturerService lecturerService;

    public MeController(LecturerService lecturerService) {

        this.lecturerService = lecturerService;
    }

    @GetMapping("/schedules")
    public ResponseEntity<?> getMySchedules(Authentication token) {
        return lecturerService.getMySchedules((JwtAuthenticationToken) token)
                .fold(error -> {
                    LOGGER.info(error.toString());
                    return ResponseEntity.badRequest().body(error);
                }, success -> ResponseEntity.status(HttpStatus.OK).body(success));
    }
}
