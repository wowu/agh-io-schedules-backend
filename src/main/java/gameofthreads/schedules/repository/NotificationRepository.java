package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
    Set<NotificationEntity> findByUser_Email_Email(String email);
}
