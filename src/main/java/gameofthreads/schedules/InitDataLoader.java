package gameofthreads.schedules;

import gameofthreads.schedules.domain.Conference;
import gameofthreads.schedules.service.ConferenceService;
import org.springframework.stereotype.Component;

@Component
public class InitDataLoader {
    private final ConferenceService conferenceService;

    public InitDataLoader(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
        loadPublicLinks();
    }

    private void loadPublicLinks(){
        Conference.loadPublicLinks(conferenceService.findPublicLinks());
    }

}
