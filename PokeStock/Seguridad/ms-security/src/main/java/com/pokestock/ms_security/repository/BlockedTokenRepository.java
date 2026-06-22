package com.pokestock.ms_security.repository;

import com.pokestock.ms_security.model.BlockedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlockedTokenRepository extends JpaRepository<BlockedToken, Long> {
    Optional<BlockedToken> findByToken(String token);
    boolean existsByToken(String token);
}
