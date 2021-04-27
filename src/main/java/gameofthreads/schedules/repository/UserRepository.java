package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.lecturer l WHERE l.email=:email")
    Optional<UserEntity> findByEmail(String email);
}
