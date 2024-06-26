package avlyakulov.timur.CloudFileStorage.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    @Modifying
    @Transactional
    @Query("update User set capacity = capacity + :capacity where id = :userId")
    void increaseUserCapacity(BigInteger capacity, Integer userId);

    @Modifying
    @Transactional
    @Query("update User set capacity = capacity - :capacity where id = :userId")
    void decreaseUserCapacity(BigInteger capacity, Integer userId);

    @Query("select capacity from User where id = ?1")
    Optional<BigInteger> findUserCapacity(Integer userId);
}