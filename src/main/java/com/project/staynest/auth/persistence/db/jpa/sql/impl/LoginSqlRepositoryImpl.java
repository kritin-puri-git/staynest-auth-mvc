package com.project.staynest.auth.persistence.db.jpa.sql.impl;

import com.project.staynest.auth.persistence.db.jpa.model.UsersCryptoRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersLookupRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersRotationData;
import com.project.staynest.auth.persistence.db.jpa.sql.repository.LoginSqlRepository;
import com.project.staynest.auth.validation.Validation;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class LoginSqlRepositoryImpl implements LoginSqlRepository {


    private static final String ROTATE_USERS_AND_USERS_LOOKUP_SQL =  """
                UPDATE
                    users AS u
                INNER JOIN users_lookup AS ul
                ON u.lookup_id = ul.id
                SET
                    u.public_id = :encryptedPublicId,
                    u.username = :encryptedUsername,
                    u.email = :encryptedEmail,
                    u.encryption_key_id = :encryptionKeyId,
                    u.encryption_version = :encryptionVersion,
                    ul.public_id_index = :publicIdIndex,
                    ul.username_index = :usernameIndex,
                    ul.email_index = :emailIndex,
                    ul.hashing_key_id = :hashingKeyId,
                    ul.hashing_version = :hashingVersion
                WHERE
                    ul.id = :id
                    AND
                    (
                        u.encryption_key_id <> :encryptionKeyId
                        OR
                        u.encryption_version <> :encryptionVersion
                    )
                    AND
                    (
                        ul.hashing_key_id <> :hashingKeyId
                        OR
                        ul.hashing_version <> :hashingVersion
                    )
                """;

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final EntityManager entityManager;

    public LoginSqlRepositoryImpl(
            EntityManager entityManager
    ){
        this.entityManager = entityManager;
    }

    @Override
    public void rotateUsersAndUsersLookupEntity(UsersCryptoRotationData usersCryptoRotationData) {
        Validation.validate(usersCryptoRotationData, "usersCryptoRotationData", CLASS_NAME);

        UsersRotationData usersRotationData = usersCryptoRotationData.usersRotationData();
        UsersLookupRotationData usersLookupRotationData = usersCryptoRotationData.usersLookupRotationData();

        Validation.validate(usersRotationData, "usersRotationData", CLASS_NAME);
        Validation.validate(usersLookupRotationData, "usersLookupRotationData", CLASS_NAME);

        long expectedRows = 2;
        long updatedRows =
                this.entityManager
                        .createNativeQuery(ROTATE_USERS_AND_USERS_LOOKUP_SQL)
                        .setParameter("id", usersCryptoRotationData.userLookupId())
                        .setParameter("encryptedPublicId", usersRotationData.encryptedPublicId())
                        .setParameter("encryptedUsername", usersRotationData.encryptedUsername())
                        .setParameter("encryptedEmail", usersRotationData.encryptedEmail())
                        .setParameter("encryptionKeyId", usersRotationData.encryptionKeyId())
                        .setParameter("encryptionVersion", usersRotationData.encryptionVersion())
                        .setParameter("publicIdIndex",usersLookupRotationData.hashedPublicId())
                        .setParameter("usernameIndex", usersLookupRotationData.hashedUsername())
                        .setParameter("emailIndex", usersLookupRotationData.hashedEmail())
                        .setParameter("hashingKeyId", usersLookupRotationData.hashingKeyId())
                        .setParameter("hashingVersion", usersLookupRotationData.hashingVersion())
                        .executeUpdate();

        if(updatedRows == 0){
            //no rows updated
            return;
        }

        if(updatedRows != expectedRows){
            throw new IllegalStateException(
                    "Unexpected affected rows: " + updatedRows +
                            " in " + CLASS_NAME
            );
        }


    }
}
