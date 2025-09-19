package com.amcclelland.ste_server.infra;


import com.amcclelland.ste_server.domain.SpotifyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpotifyAccountRepository extends JpaRepository<SpotifyAccount, Long> {
    Optional<SpotifyAccount> findByUserId(Long userId);
}
