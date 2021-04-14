package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ConferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Integer> {
}
