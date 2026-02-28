package ineventory.Repository;

import ineventory.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);

    //boolean existsByRole(Role role);       // For admin existence check
    List<User> findByStatus(String status); // For pending users
}
