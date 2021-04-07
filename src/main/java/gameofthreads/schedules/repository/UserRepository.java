package gameofthreads.schedules.repository;

import gameofthreads.schedules.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT a FROM User a inner join a.person ap where ap.email=:email")
    Optional<User> findByEmail(String email);
}
