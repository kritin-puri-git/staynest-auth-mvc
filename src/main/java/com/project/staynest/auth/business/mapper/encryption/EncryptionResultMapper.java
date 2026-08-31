package com.project.staynest.auth.business.mapper.encryption;

import com.project.staynest.auth.crypto.encryption.model.EncryptionResult;
import com.project.staynest.auth.persistence.db.jpa.projection.UserDataProjection;
import com.project.staynest.auth.persistence.db.jpa.projection.UserEmailProjection;

public final class EncryptionResultMapper {

    private EncryptionResultMapper() {}

    public static EncryptionResult from(UserEmailProjection emailLoginVerificationData){
        return new EncryptionResult(
                emailLoginVerificationData.encryptedEmail(),
                emailLoginVerificationData.encryptionKeyId(),
                emailLoginVerificationData.encryptionVersion()
        );
    }

    public static EncryptionResult from(UserDataProjection userLoginData){
        return new EncryptionResult(
                userLoginData.encryptedEmail(),
                userLoginData.encryptionKeyId(),
                userLoginData.encryptionVersion()
        );
    }
}
