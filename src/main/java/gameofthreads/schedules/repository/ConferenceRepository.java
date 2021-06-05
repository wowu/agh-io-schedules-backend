package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Integer> {
    Long countConferenceEntitiesBySchedule(ScheduleEntity schedule);
    List<ConferenceEntity> findAllBySchedule(ScheduleEntity schedule);

    @Query("SELECT c FROM ConferenceEntity AS c INNER JOIN FETCH c.schedule INNER JOIN FETCH c.meetingEntities")
    Set<ConferenceEntity> fetchWithScheduleAndMeetings();
}
