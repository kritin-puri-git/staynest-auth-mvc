package com.project.staynest.auth.business.mapper.login;

import com.project.staynest.auth.business.model.login.LoginByEmailModel;
import com.project.staynest.auth.presentation.dto.request.LoginRequestDto;

public final class LoginByEmailModelMapper {
    private LoginByEmailModelMapper(){}

    public static LoginByEmailModel from(LoginRequestDto loginRequestDto){
        return new LoginByEmailModel(loginRequestDto.getEmail());
    }

}