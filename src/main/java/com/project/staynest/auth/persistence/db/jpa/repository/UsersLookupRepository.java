package com.project.staynest.auth.persistence.db.jpa.repository;

import com.project.staynest.auth.persistence.db.jpa.entity.UsersLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersLookupRepository
        extends JpaRepository<UsersLookupEntity, Long> {

    boolean existsByPublicIdIndexIn(List<byte[]> publicIdIndexes);
    boolean existsByUsernameIndexIn(List<byte[]> usernameIndexes);
    boolean existsByEmailIndexIn(List<byte[]> emailIndexes);

}
