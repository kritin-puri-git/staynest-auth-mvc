package com.project.staynest.auth.persistence.db.port;

import com.project.staynest.auth.persistence.db.jpa.model.UserData;

import java.util.List;

public interface SignupRepositoryPort {
    void saveUserData(UserData userData);
    boolean checkPublicIdIndexExists(List<byte[]> publicIdIndexes);
    boolean checkUsernameIndexExists(List<byte[]> usernameIndexes);
    boolean checkEmailIndexExists(List<byte[]> emailIndexes);

}