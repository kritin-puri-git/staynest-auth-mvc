package com.project.staynest.auth.persistence.db.jpa.repository;

import com.project.staynest.auth.persistence.db.jpa.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsersRepository
        extends JpaRepository<UsersEntity, Long> {
}
