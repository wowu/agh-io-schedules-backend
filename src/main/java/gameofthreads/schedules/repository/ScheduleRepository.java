package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {
    @Query("SELECT s FROM ScheduleEntity s LEFT JOIN FETCH s.subscriptions WHERE s.id=:scheduleId")
    Optional<ScheduleEntity> fetchWithSubscriptions(Integer scheduleId);

    @Query("SELECT s FROM ScheduleEntity s LEFT JOIN FETCH s.conferenceEntities as c LEFT JOIN FETCH c.meetingEntities WHERE s.id=:scheduleId")
    Optional<ScheduleEntity> fetchWithConferencesAndMeetings(Integer scheduleId);

    @Query("SELECT s FROM ScheduleEntity s LEFT JOIN FETCH s.conferenceEntities as c LEFT JOIN FETCH c.meetingEntities")
    Set<ScheduleEntity> fetchAllWithConferencesAndMeetings();

    @Modifying
    @Query("UPDATE ScheduleEntity s SET s.description=:description, s.fileName=:fileName where s.id=:scheduleId")
    void updateAllMetadata(Integer scheduleId, @Param(value = "fileName") String fileName, @Param(value = "description") String description);

    @Modifying
    @Query("UPDATE ScheduleEntity s SET s.fileName=:fileName where s.id=:scheduleId")
    void updateFilenameMetadata(Integer scheduleId, @Param(value = "fileName") String fileName);

    @Modifying
    @Query("UPDATE ScheduleEntity s SET s.description=:description where s.id=:scheduleId")
    void updateDescriptionMetadata(Integer scheduleId, @Param(value = "description") String description);
}
