package com.project.staynest.auth.persistence.db.jpa.mapper;

import com.project.staynest.auth.crypto.hashing.model.HashingResult;
import com.project.staynest.auth.persistence.db.jpa.model.EmailIndexData;

import java.util.ArrayList;
import java.util.List;

public final class EmailIndexDataMapper {
    private EmailIndexDataMapper(){}

    public static List<EmailIndexData> from(List<HashingResult> hashDataList){
        List<EmailIndexData> emailIndexDataList = new ArrayList<>(hashDataList.size());
        for(HashingResult hashData : hashDataList){
            emailIndexDataList.add(
                    new EmailIndexData(
                            hashData.hash(),
                            hashData.keyId(),
                            hashData.version()
                    )
            );
        }

        return emailIndexDataList;

    }
}
