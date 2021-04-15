package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {
    Optional<ScheduleEntity> findByPublicLink(String publicLink);
}
