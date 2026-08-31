package com.project.staynest.auth.persistence.db.jpa.querydsl.impl;

import com.project.staynest.auth.persistence.db.jpa.entity.QJwtEntity;
import com.project.staynest.auth.persistence.db.jpa.model.JwtRotationData;
import com.project.staynest.auth.persistence.db.jpa.querydsl.repository.JwtQueryRepository;
import com.project.staynest.auth.validation.Validation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

@Repository
public class JwtQueryRepositoryImpl implements JwtQueryRepository {

    public static final QJwtEntity JWT_ENTITY = QJwtEntity.jwtEntity;

    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final JPAQueryFactory jpaQueryFactory;
    public JwtQueryRepositoryImpl(
            JPAQueryFactory jpaQueryFactory
    ){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public void rotateJwt(JwtRotationData jwtRotationData) {
        Validation.validate(jwtRotationData, "jwtRotationData", CLASS_NAME);

        long expectedRows = 1;

        long updatedRows =
                this.jpaQueryFactory
                        .update(JWT_ENTITY)
                        .set(JWT_ENTITY.accessJwtId, jwtRotationData.accessJwtTokenId())
                        .set(JWT_ENTITY.refreshJwtId, jwtRotationData.refreshJwtTokenId())
                        .set(JWT_ENTITY.refreshExpiresAt, jwtRotationData.refreshExpirationTime())
                        .where(
                                JWT_ENTITY.subject.eq(jwtRotationData.subject())
                                        .and(JWT_ENTITY.sessionId.eq(jwtRotationData.sessionId()))
                        )
                        .execute();

        if(updatedRows == 0){
            //no rows updated
            return;
        }

        if(updatedRows != expectedRows){
            throw new IllegalStateException(
                    "Multiple jwt rows updated in " + CLASS_NAME
            );
        }
    }
}

//
//UPDATE jwt
//SET
//access_jwt_id = ?,
//refresh_jwt_id = ?,
//refresh_expires_at = ?
//WHERE
//subject = ?
//AND session_id = ?
