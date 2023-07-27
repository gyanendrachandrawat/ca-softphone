package com.consultadd.repository;

import com.consultadd.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClientId(UUID clientId);

    Optional<User> findByTwilioNumber(String twilioNumber);

    Optional<User> findByEmailIgnoreCase(String email);
}
