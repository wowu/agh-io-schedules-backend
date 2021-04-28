package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ConferenceEntity;
import gameofthreads.schedules.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface MeetingRepository extends JpaRepository<MeetingEntity, Integer> {
    Long countMeetingEntitiesByConference(ConferenceEntity conference);

    @Query("SELECT m FROM MeetingEntity AS m WHERE m.dateStart BETWEEN :start AND :end")
    Set<MeetingEntity> findTomorrowsMeetings(LocalDateTime start, LocalDateTime end);
}
