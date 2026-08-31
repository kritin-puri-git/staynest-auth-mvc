package com.project.staynest.auth.persistence.db.jpa.repository;


import com.project.staynest.auth.persistence.db.jpa.entity.JwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface JwtRepository
        extends JpaRepository<JwtEntity, Long> {
    Optional<JwtEntity> findBySubjectAndSessionId(String subject, String sessionId);
}
