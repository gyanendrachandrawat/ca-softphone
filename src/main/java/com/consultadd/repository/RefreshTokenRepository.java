package com.consultadd.repository;

import com.consultadd.model.RefreshToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    List<RefreshToken> findByUser_IdEqualsAndIsValidIsTrue(Long userId);
}
