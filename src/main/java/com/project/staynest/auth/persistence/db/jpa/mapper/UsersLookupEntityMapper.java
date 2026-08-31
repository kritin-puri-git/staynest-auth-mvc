package com.project.staynest.auth.persistence.db.jpa.mapper;

import com.project.staynest.auth.persistence.db.jpa.entity.UsersLookupEntity;
import com.project.staynest.auth.persistence.db.jpa.model.UserData;

public final class UsersLookupEntityMapper {
    private UsersLookupEntityMapper(){}

    public static UsersLookupEntity from(UserData userData){

        return UsersLookupEntity
                .builder()
                .publicIdIndex(userData.publicIdHash())
                .usernameIndex(userData.usernameHash())
                .emailIndex(userData.emailHash())
                .hashingKeyId(userData.hashingKeyId())
                .hashingVersion(userData.hashingVersion())
                .build();
    }
}
