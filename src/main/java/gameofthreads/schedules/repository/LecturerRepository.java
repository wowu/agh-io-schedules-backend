package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.LecturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LecturerRepository extends JpaRepository<LecturerEntity, Integer> {
    Optional<LecturerEntity> findByEmail_Email(String email);

    @Query("SELECT l FROM LecturerEntity AS l LEFT JOIN FETCH l.email AS le LEFT JOIN FETCH le.lecturer")
    Set<LecturerEntity> fetchWithUser();
}
