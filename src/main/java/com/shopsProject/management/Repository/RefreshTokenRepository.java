package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.RefreshToken;
import com.shopsProject.management.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUser(User user);
}
