package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.ScheduleEntity;
import gameofthreads.schedules.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Integer> {
}
