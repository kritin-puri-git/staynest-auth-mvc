package com.project.staynest.auth.presentation.mapper;

import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.presentation.dto.response.SigninResponseDTO;

public class SigninRespnoseDTOMapper {
    private SigninRespnoseDTOMapper(){}

    public static SigninResponseDTO from(SigninData signinData){

        return new SigninResponseDTO(
                signinData.username(),
                signinData.email()
        );
    }
}
