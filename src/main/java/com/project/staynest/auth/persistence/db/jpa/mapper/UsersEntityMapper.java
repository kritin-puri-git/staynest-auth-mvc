package com.project.staynest.auth.persistence.db.jpa.mapper;

import com.project.staynest.auth.persistence.db.jpa.entity.UsersEntity;
import com.project.staynest.auth.persistence.db.jpa.model.UserData;

public final class UsersEntityMapper {
    private UsersEntityMapper(){}

    public static UsersEntity from(UserData userData){
        return UsersEntity
                .builder()
                .publicId(userData.encryptedPublicId())
                .username(userData.encryptedUsername())
                .email(userData.encryptedEmail())
                .encryptionKeyId(userData.encryptionKeyId())
                .encryptionVersion(userData.encryptionVersion())
                .status(userData.status())
                .build();
    }
}
