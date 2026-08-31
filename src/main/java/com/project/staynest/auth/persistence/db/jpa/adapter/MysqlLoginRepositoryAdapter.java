package com.project.staynest.auth.persistence.db.jpa.adapter;

import com.project.staynest.auth.persistence.db.jpa.model.UsersCryptoRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.EmailIndexData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersLookupRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersRotationData;
import com.project.staynest.auth.persistence.db.jpa.projection.UserDataProjection;
import com.project.staynest.auth.persistence.db.jpa.projection.UserEmailProjection;
import com.project.staynest.auth.persistence.db.jpa.querydsl.repository.LoginQueryRepository;
import com.project.staynest.auth.persistence.db.jpa.sql.repository.LoginSqlRepository;
import com.project.staynest.auth.persistence.db.port.LoginRepositoryPort;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlLoginRepositoryAdapter implements LoginRepositoryPort {

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final LoginQueryRepository loginQueryRepository;
    private final LoginSqlRepository loginSqlRepository;

    public MysqlLoginRepositoryAdapter(
            LoginQueryRepository loginQueryRepository,
            LoginSqlRepository loginSqlRepository
    ){
        this.loginQueryRepository = loginQueryRepository;
        this.loginSqlRepository = loginSqlRepository;
    }

    @Override
    public List<UserEmailProjection> getUsersEmail(List<EmailIndexData> emailIndexData) {
        Validation.validate(emailIndexData, "emailIndexData", CLASS_NAME);

        return this.loginQueryRepository.findUsersEmail(
                emailIndexData
        );

    }

    @Override
    public List<UserDataProjection> getUsersData(List<EmailIndexData> emailIndexData) {
        Validation.validate(emailIndexData, "emailIndexData", CLASS_NAME);

        return this.loginQueryRepository.findUsersData(
                emailIndexData
        );
    }

    @Override
    public void rotateUsersCrypto(UsersCryptoRotationData usersCryptoRotationData) {
        Validation.validate(
                usersCryptoRotationData,
                "usersCryptoRotationData",
                CLASS_NAME
        );
        Validation.validate(
                usersCryptoRotationData.userLookupId(),
                "userLookupId",
                CLASS_NAME
        );

        final UsersRotationData usersRotationData =
                usersCryptoRotationData.usersRotationData();
        final UsersLookupRotationData usersLookupRotationData =
                usersCryptoRotationData.usersLookupRotationData();

        if (usersRotationData != null
                && usersLookupRotationData != null){

            this.rotateUsersAndUsersLookupEntity(
                    usersCryptoRotationData
            );
            return;
        }

        if(usersRotationData != null){

            this.rotateUsersEntity(
                    usersCryptoRotationData.userLookupId(),
                    usersRotationData
            );
            return;
        }

        if(usersLookupRotationData != null){

            this.rotateUsersLookupEntity(
                    usersCryptoRotationData.userLookupId(),
                    usersLookupRotationData
            );
            return;
        }

        throw new IllegalStateException(
                "No crypto rotation data available in " + CLASS_NAME
        );

    }

    private void rotateUsersEntity(
            Long userLookupId,
            UsersRotationData usersRotationData
    ){
        Validation.validate(usersRotationData, "usersRotationData", CLASS_NAME);

        this.loginQueryRepository.rotateUsersEntity(
                userLookupId,
                usersRotationData
        );
    }

    private void rotateUsersLookupEntity(
            Long userLookupId,
            UsersLookupRotationData usersLookupRotationData
    ){
        Validation.validate(usersLookupRotationData, "usersLookupRotationData", CLASS_NAME);

        this.loginQueryRepository.rotateUsersLookupEntity(
                userLookupId,
                usersLookupRotationData
        );
    }

    private void rotateUsersAndUsersLookupEntity(
            UsersCryptoRotationData usersCryptoRotationData
    ){
        Validation.validate(usersCryptoRotationData, "usersCryptoRotationData", CLASS_NAME);
        this.loginSqlRepository.rotateUsersAndUsersLookupEntity(
                usersCryptoRotationData
        );
    }

}
