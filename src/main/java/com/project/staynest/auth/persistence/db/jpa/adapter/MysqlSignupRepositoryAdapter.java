package com.project.staynest.auth.persistence.db.jpa.adapter;

import com.project.staynest.auth.persistence.db.jpa.entity.UsersEntity;
import com.project.staynest.auth.persistence.db.jpa.entity.UsersLookupEntity;
import com.project.staynest.auth.persistence.db.jpa.mapper.UsersEntityMapper;
import com.project.staynest.auth.persistence.db.jpa.mapper.UsersLookupEntityMapper;
import com.project.staynest.auth.persistence.db.jpa.model.UserData;
import com.project.staynest.auth.persistence.db.jpa.repository.UsersLookupRepository;
import com.project.staynest.auth.persistence.db.jpa.repository.UsersRepository;
import com.project.staynest.auth.persistence.db.port.SignupRepositoryPort;
import com.project.staynest.auth.validation.Validation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlSignupRepositoryAdapter implements SignupRepositoryPort {

    private final String CLASS_NAME = this.getClass().getSimpleName();
    private final UsersRepository usersRepository;
    private final UsersLookupRepository usersLookupRepository;
    public MysqlSignupRepositoryAdapter(
            UsersRepository usersRepository,
            UsersLookupRepository usersLookupRepository
    ){
        this.usersRepository = usersRepository;
        this.usersLookupRepository = usersLookupRepository;
    }

    @Override
    public void saveUserData(UserData userData) {
        Validation.validate(userData, "userData", CLASS_NAME);

        UsersLookupEntity usersLookupEntity = UsersLookupEntityMapper.from(
                userData
        );
        UsersEntity usersEntity = UsersEntityMapper.from(
                userData
        );

        usersEntity.setUsersLookupEntity(
                usersLookupEntity
        );

        this.usersRepository.save(usersEntity);

    }

    @Override
    public boolean checkPublicIdIndexExists(List<byte[]> publicIdIndexes) {
        Validation.validate(publicIdIndexes, "publicIdIndexes", CLASS_NAME);
        return this.usersLookupRepository.existsByPublicIdIndexIn(
                publicIdIndexes
        );
    }

    @Override
    public boolean checkUsernameIndexExists(List<byte[]> usernameIndexes) {
        Validation.validate(usernameIndexes, "usernameIndexes", CLASS_NAME);
        return this.usersLookupRepository.existsByUsernameIndexIn(
                usernameIndexes
        );
    }

    @Override
    public boolean checkEmailIndexExists(List<byte[]> emailIndexes) {
        Validation.validate(emailIndexes, "emailIndexes", CLASS_NAME);
        return this.usersLookupRepository.existsByEmailIndexIn(
                emailIndexes
        );
    }


}
