package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailEntity, Integer> {
    Optional<EmailEntity> findByEmail(String email);
}
