package com.project.staynest.auth.persistence.db.jpa.querydsl.repository;

import com.project.staynest.auth.persistence.db.jpa.model.EmailIndexData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersLookupRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersRotationData;
import com.project.staynest.auth.persistence.db.jpa.projection.UserDataProjection;
import com.project.staynest.auth.persistence.db.jpa.projection.UserEmailProjection;

import java.util.List;

public interface LoginQueryRepository {
    List<UserEmailProjection> findUsersEmail(List<EmailIndexData> emailIndexData);
    List<UserDataProjection> findUsersData(List<EmailIndexData> emailIndexData);
    void rotateUsersEntity(Long userLookupId, UsersRotationData usersRotationData);
    void rotateUsersLookupEntity(Long id, UsersLookupRotationData usersLookupRotationData);

}
