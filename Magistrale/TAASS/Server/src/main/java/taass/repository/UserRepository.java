package taass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taass.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Optional<User> findByEmail(String email);

    Boolean existsByUserName(String username);

    Optional<User> findByUserName(String username);

}
