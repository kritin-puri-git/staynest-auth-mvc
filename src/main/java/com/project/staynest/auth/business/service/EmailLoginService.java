package com.project.staynest.auth.business.service;

import com.project.staynest.auth.business.model.SentOtpData;
import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.business.model.login.LoginByEmailModel;
import com.project.staynest.auth.business.model.login.VerifyLoginByEmailModel;

public interface EmailLoginService {

    SentOtpData loginByEmail(LoginByEmailModel loginByEmailModel);
    SigninData verifyLogin(VerifyLoginByEmailModel verifyLoginByEmailModel);
}
