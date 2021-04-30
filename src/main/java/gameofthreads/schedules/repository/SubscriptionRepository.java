package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Integer> {
    @Query("SELECT sub from SubscriptionEntity AS sub LEFT JOIN FETCH " +
            "sub.schedule AS schedule LEFT JOIN FETCH schedule.conferenceEntities AS conferences " +
            "LEFT JOIN FETCH conferences.meetingEntities AS meeting " +
            "WHERE sub.active = true " +
            "AND meeting.dateStart BETWEEN :start AND :end")
    Set<SubscriptionEntity> fetchAll(LocalDateTime start, LocalDateTime end);

    Set<SubscriptionEntity> findBySchedule_Id(Integer scheduleId);
}
