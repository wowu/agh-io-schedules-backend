package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Integer> {
    Long countConferenceEntitiesBySchedule(ScheduleEntity schedule);

    List<ConferenceEntity> findAllBySchedule(ScheduleEntity schedule);
}
