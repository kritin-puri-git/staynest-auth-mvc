package com.project.staynest.auth.business.mapper.signup;

import com.project.staynest.auth.business.model.signup.SignupModel;
import com.project.staynest.auth.presentation.dto.request.SignupRequestDTO;

public final class SignupModelMapper {
    private SignupModelMapper(){

    }


    public static SignupModel from(SignupRequestDTO dto){
        return new SignupModel(
                dto.getUsername(),
                dto.getEmail()
        );
    }

}
