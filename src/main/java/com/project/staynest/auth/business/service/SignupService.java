package com.project.staynest.auth.business.service;

import com.project.staynest.auth.business.model.SentOtpData;
import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.business.model.signup.SignupModel;
import com.project.staynest.auth.business.model.signup.VerifySignupModel;

public interface SignupService {

    SentOtpData signup(SignupModel signupModel);
    SigninData verifySignup(VerifySignupModel verifySignupModel);
}
