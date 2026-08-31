package com.project.staynest.auth.persistence.db.jpa.querydsl.impl;

import com.project.staynest.auth.persistence.db.jpa.entity.QUsersEntity;
import com.project.staynest.auth.persistence.db.jpa.entity.QUsersLookupEntity;
import com.project.staynest.auth.persistence.db.jpa.model.EmailIndexData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersLookupRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersRotationData;
import com.project.staynest.auth.persistence.db.jpa.projection.UserDataProjection;
import com.project.staynest.auth.persistence.db.jpa.projection.UserEmailProjection;
import com.project.staynest.auth.persistence.db.jpa.querydsl.repository.LoginQueryRepository;
import com.project.staynest.auth.validation.Validation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LoginQueryRepositoryImpl implements LoginQueryRepository {

    private final static QUsersEntity USERS = QUsersEntity.usersEntity;
    private final static QUsersLookupEntity USERS_LOOKUP = QUsersLookupEntity.usersLookupEntity;

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final JPAQueryFactory jpaQueryFactory;
    public LoginQueryRepositoryImpl(
            JPAQueryFactory jpaQueryFactory
    ){
        this.jpaQueryFactory = jpaQueryFactory;
    }
    
    @Override
    public List<UserEmailProjection> findUsersEmail(List<EmailIndexData> emailIndexData) {
        Validation.validate(emailIndexData, "emailIndexData", CLASS_NAME);

        BooleanBuilder emailLookupPredicate = this.getEmailLookupPredicate(
                emailIndexData
        );


         return jpaQueryFactory
                 .select(
                         Projections.constructor(
                                 UserEmailProjection.class,
                                 USERS.email,
                                 USERS.encryptionKeyId,
                                 USERS.encryptionVersion
                         )

                 )
                 .from(USERS_LOOKUP)
                 .innerJoin(USERS)
                 .on(USERS_LOOKUP.id.eq(USERS.userLookupId))
                 .where(emailLookupPredicate)
                 .fetch();

    }

    @Override
    public List<UserDataProjection> findUsersData(List<EmailIndexData> emailIndexData) {
        Validation.validate(emailIndexData, "emailIndexData", CLASS_NAME);

        BooleanBuilder emailLookupPredicate = getEmailLookupPredicate(
                emailIndexData
        );

        return this.jpaQueryFactory
                .select(
                        Projections.constructor(
                                UserDataProjection.class,
                                USERS.userLookupId,
                                USERS.publicId,
                                USERS.username,
                                USERS.email,
                                USERS.encryptionKeyId,
                                USERS.encryptionVersion,
                                USERS_LOOKUP.hashingKeyId,
                                USERS_LOOKUP.hashingVersion,
                                USERS.status
                        )
                )
                .from(USERS_LOOKUP)
                .innerJoin(USERS)
                .on(USERS_LOOKUP.id.eq(USERS.userLookupId))
                .where(emailLookupPredicate)
                .fetch();
    }

    private BooleanBuilder getEmailLookupPredicate(
            List<EmailIndexData> emailIndexData
    ){
        Validation.validate(emailIndexData, "emailIndexData", CLASS_NAME);
        BooleanBuilder emailLookupPredicate = new BooleanBuilder();

        for(EmailIndexData data : emailIndexData){
            emailLookupPredicate.or(
                    USERS_LOOKUP.emailIndex.eq(data.emailIndex())
                            .and(
                                    USERS_LOOKUP.hashingKeyId.eq(data.keyId())
                            )
                            .and(
                                    USERS_LOOKUP.hashingVersion.eq(data.version())
                            )
            );
        }

        return emailLookupPredicate;
    }

    @Override
    public void rotateUsersEntity(Long userLookupId, UsersRotationData usersRotationData) {
        Validation.validate(userLookupId, "userLookupId", CLASS_NAME);
        Validation.validate(usersRotationData, "usersRotationData", CLASS_NAME);

        long expectedRows = 1;
        long updatedRows =
                this.jpaQueryFactory
                        .update(USERS)
                        .set(USERS.publicId, usersRotationData.encryptedPublicId())
                        .set(USERS.username, usersRotationData.encryptedUsername())
                        .set(USERS.email, usersRotationData.encryptedEmail())
                        .set(USERS.encryptionKeyId, usersRotationData.encryptionKeyId())
                        .set(USERS.encryptionVersion, usersRotationData.encryptionVersion())
                        .where(
                                USERS.userLookupId.eq(userLookupId)
                                        .and(
                                                USERS.encryptionKeyId
                                                        .ne(usersRotationData.encryptionKeyId())
                                                        .or(
                                                                USERS.encryptionVersion
                                                                        .ne(usersRotationData.encryptionVersion())
                                                        )


                                        )
                        )
                        .execute();

        if(updatedRows == 0){
            //no rows updated
            return;
        }

        if(updatedRows != expectedRows){
            throw new IllegalStateException(
                    "Multiple users updated in " + CLASS_NAME
            );
        }
    }

    @Override
    public void rotateUsersLookupEntity(Long id, UsersLookupRotationData usersLookupRotationData) {
        Validation.validate(id, "id", CLASS_NAME);
        Validation.validate(usersLookupRotationData, "usersLookupRotationData", CLASS_NAME);

        long expectedRows = 1;
        long updatedRows =
                this.jpaQueryFactory
                        .update(USERS_LOOKUP)
                        .set(USERS_LOOKUP.publicIdIndex, usersLookupRotationData.hashedPublicId())
                        .set(USERS_LOOKUP.usernameIndex, usersLookupRotationData.hashedUsername())
                        .set(USERS_LOOKUP.emailIndex, usersLookupRotationData.hashedEmail())
                        .set(USERS_LOOKUP.hashingKeyId, usersLookupRotationData.hashingKeyId())
                        .set(USERS_LOOKUP.hashingVersion, usersLookupRotationData.hashingVersion())
                        .where(USERS_LOOKUP.id.eq(id)
                                .and(
                                        USERS_LOOKUP.hashingKeyId.ne(
                                                        usersLookupRotationData.hashingKeyId()
                                                )
                                                .or(
                                                        USERS_LOOKUP.hashingVersion.ne(
                                                                usersLookupRotationData.hashingVersion()
                                                        )
                                                )
                                )
                        )
                        .execute();

        if(updatedRows == 0){
            //no rows updated
            // Might Already on latest crypto version
            return;
        }

        if(updatedRows != expectedRows){
            throw new IllegalStateException(
                    "Multiple users updated in " + CLASS_NAME
            );
        }

    }
}

/*
 * Equivalent SQL:
 *
 * SELECT
 *      user.identifier,
 *      user.encryption_key_id,
 *      user.encryption_version
 *
 * FROM users_lookup AS lookup
 *
 * INNER JOIN users AS user
 *      ON lookup.userLookupId = user.lookup_id
 *
 * WHERE
 *      (
 *          lookup.email_index = ?
 *          AND lookup.hashing_key_id = ?
 *          AND lookup.hashing_version = ?
 *      )
 *      OR
 *      (
 *          lookup.email_index = ?
 *          AND lookup.hashing_key_id = ?
 *          AND lookup.hashing_version = ?
 *      )
 *      OR
 *      (
 *          lookup.email_index = ?
 *          AND lookup.hashing_key_id = ?
 *          AND lookup.hashing_version = ?
 *      )
 *      OR
 *      (
 *          lookup.email_index = ?
 *          AND lookup.hashing_key_id = ?
 *          AND lookup.hashing_version = ?
 *      )
 *      ...
 */

/*
* UPDATE
*   users
* Set
*   users.public_id = ?
*   users.username = ?
*   users.identifier = ?
* WHERE
*   users.lookup_id = ?
*   AND
*   (
*       users.encryption_key_id <> ?
*       OR
*       users.encryption_version <> ?
*   );
**/