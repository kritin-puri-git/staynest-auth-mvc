package com.project.staynest.auth.persistence.db.jpa.querydsl.repository;

import com.project.staynest.auth.persistence.db.jpa.model.JwtRotationData;

public interface JwtQueryRepository {

    void rotateJwt(JwtRotationData jwtRotationData);
}
