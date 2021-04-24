package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.LecturerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerRepository extends JpaRepository<LecturerEntity, Integer> {
}
