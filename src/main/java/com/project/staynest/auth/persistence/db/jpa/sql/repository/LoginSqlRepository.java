package com.project.staynest.auth.persistence.db.jpa.sql.repository;

import com.project.staynest.auth.persistence.db.jpa.model.UsersCryptoRotationData;

public interface LoginSqlRepository {
    void rotateUsersAndUsersLookupEntity(UsersCryptoRotationData usersCryptoRotationData);
}
