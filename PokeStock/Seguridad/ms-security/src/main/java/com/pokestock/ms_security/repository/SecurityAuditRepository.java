package com.pokestock.ms_security.repository;

import com.pokestock.ms_security.model.SecurityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SecurityAuditRepository extends JpaRepository<SecurityAudit, Long> {
    List<SecurityAudit> findByUsername(String username);
    List<SecurityAudit> findByAction(String action);
}
