package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionService extends JpaRepository<SubscriptionEntity,Integer> {
}
