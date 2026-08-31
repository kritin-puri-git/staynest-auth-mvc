package com.project.staynest.auth.persistence.db.port;

import com.project.staynest.auth.persistence.db.jpa.model.UsersCryptoRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.EmailIndexData;
import com.project.staynest.auth.persistence.db.jpa.projection.UserDataProjection;
import com.project.staynest.auth.persistence.db.jpa.projection.UserEmailProjection;

import java.util.List;

public interface LoginRepositoryPort {
    List<UserEmailProjection> getUsersEmail(List<EmailIndexData> emailIndexData);
    List<UserDataProjection> getUsersData(List<EmailIndexData> emailIndexData);
    void rotateUsersCrypto(UsersCryptoRotationData usersCryptoRotationData);
}
