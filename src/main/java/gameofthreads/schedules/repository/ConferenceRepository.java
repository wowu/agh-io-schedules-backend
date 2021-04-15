package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ConferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Integer> {
    Optional<ConferenceEntity> findByPublicLink(String publicLink);
}
