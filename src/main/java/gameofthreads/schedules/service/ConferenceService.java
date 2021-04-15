package gameofthreads.schedules.service;

import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.repository.ConferenceRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public Set<String> findPublicLinks(){
        return conferenceRepository.findAll()
                .stream()
                .map(ConferenceEntity::getPublicLink)
                .collect(Collectors.toSet());
    }

}
