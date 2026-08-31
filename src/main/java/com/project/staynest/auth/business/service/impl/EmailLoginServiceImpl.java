package com.project.staynest.auth.business.service.impl;

import com.project.staynest.auth.business.constants.OtpBusinessConstants;
import com.project.staynest.auth.business.enums.Purpose;
import com.project.staynest.auth.business.model.login.LoginByEmailModel;
import com.project.staynest.auth.business.model.login.VerifyLoginByEmailModel;
import com.project.staynest.auth.enums.UserStatus;
import com.project.staynest.auth.business.facade.CryptoFacade;
import com.project.staynest.auth.business.mapper.encryption.EncryptionResultMapper;
import com.project.staynest.auth.business.model.SentOtpData;
import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.business.model.login.*;
import com.project.staynest.auth.business.service.EmailLoginService;
import com.project.staynest.auth.business.service.JwtTokenService;
import com.project.staynest.auth.crypto.encryption.model.EncryptionResultMap;
import com.project.staynest.auth.crypto.hashing.model.HashingResult;
import com.project.staynest.auth.crypto.hashing.model.HashingResultMap;
import com.project.staynest.auth.errorhandling.exceptions.business.authentication.UserNotFoundAfterVerificationException;
import com.project.staynest.auth.errorhandling.exceptions.business.authentication.UserNotFoundException;
import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.otp.model.EmailOtpData;
import com.project.staynest.auth.otp.model.VerifyOtpDetails;
import com.project.staynest.auth.otp.service.EmailOtpService;
import com.project.staynest.auth.otp.service.OtpService;
import com.project.staynest.auth.persistence.db.jpa.mapper.EmailIndexDataMapper;
import com.project.staynest.auth.persistence.db.jpa.model.UsersCryptoRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersLookupRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.UsersRotationData;
import com.project.staynest.auth.persistence.db.jpa.projection.UserDataProjection;
import com.project.staynest.auth.persistence.db.jpa.projection.UserEmailProjection;
import com.project.staynest.auth.persistence.db.port.LoginRepositoryPort;
import com.project.staynest.auth.validation.Validation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailLoginServiceImpl implements EmailLoginService {
    private final static Purpose PURPOSE = Purpose.LOGIN;
    private final static String MAP_KEY_USERNAME = "username";
    private final static String MAP_KEY_EMAIL = "identifier";
    private final static String MAP_KEY_PUBLIC_ID = "publicId";
    private final static int OTP_EXPIRY_SECONDS = OtpBusinessConstants.OTP_EXPIRY_SECONDS;
    private final static int OTP_TTL_SECONDS = OtpBusinessConstants.OTP_TTL_SECONDS;

    private final static Logger logger = LoggerFactory.getLogger(EmailLoginServiceImpl.class);

    private final String className = this.getClass().getSimpleName();

    private final CryptoFacade cryptoFacade;
    private final LoginRepositoryPort loginRepositoryPort;
    private final EmailOtpService emailOtpService;
    private final OtpService otpService;
    private final JwtTokenService jwtTokenService;
    public EmailLoginServiceImpl(
            CryptoFacade cryptoFacade,
            LoginRepositoryPort loginRepositoryPort,
            EmailOtpService emailOtpService,
            OtpService otpService,
            JwtTokenService jwtTokenService
    ){
        this.cryptoFacade = cryptoFacade;
        this.loginRepositoryPort = loginRepositoryPort;
        this.emailOtpService = emailOtpService;
        this.otpService = otpService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public SentOtpData loginByEmail(LoginByEmailModel loginByEmailModel) {
        Validation.validate(loginByEmailModel, "loginByEmailModel", className);

        this.verifyUserByEmail(
                loginByEmailModel.email()
        );

        String token = UUID.randomUUID().toString();

        this.sendOtp(
                loginByEmailModel.email(),
                token
        );

        return new SentOtpData(
                loginByEmailModel.email(),
                token
        );
    }

    @Transactional
    @Override
    public SigninData verifyLogin(VerifyLoginByEmailModel verifyLoginByEmailModel) {
        Validation.validate(verifyLoginByEmailModel, "verifyLoginByEmailModel", className);

        String identifier = verifyLoginByEmailModel.email();
        this.verifyOtp(
                identifier,
                verifyLoginByEmailModel.token(),
                verifyLoginByEmailModel.otp()
        );

        UserDataProjection userLoginData = this.getVerifiedUser(
                verifyLoginByEmailModel.email()
        );
        Map<String, byte[]> encryptedDataMap = Map.of(
                MAP_KEY_PUBLIC_ID, userLoginData.encryptedPublicId(),
                MAP_KEY_USERNAME, userLoginData.encryptedUsername(),
                MAP_KEY_EMAIL, userLoginData.encryptedEmail()
        );
        Map<String, String> plainDataMap = this.cryptoFacade.decryptDataMap(
                encryptedDataMap,
                userLoginData.encryptionKeyId(),
                userLoginData.encryptionVersion()
        );

        this.rotateUserDataCrypto(userLoginData, plainDataMap);

        UserStatus status = UserStatus.from(userLoginData.status());
        return new SigninData(
                this.jwtTokenService.generateUserTokenDetails(
                        plainDataMap.get(MAP_KEY_PUBLIC_ID),
                        status,
                        verifyLoginByEmailModel.deviceId(),
                        verifyLoginByEmailModel.userAgent()
                ),
                plainDataMap.get(MAP_KEY_PUBLIC_ID),
                plainDataMap.get(MAP_KEY_USERNAME),
                plainDataMap.get(MAP_KEY_EMAIL)
        );
    }

    private void verifyUserByEmail(String email){
        List<HashingResult> hashDataList = this.cryptoFacade.getDetailedHashCandidates(
                email
        );

        List<UserEmailProjection> emailLoginVerificationDataList =
                this.loginRepositoryPort.getUsersEmail(
                        EmailIndexDataMapper.from(
                                hashDataList
                        )
                );

        if(emailLoginVerificationDataList == null
                || emailLoginVerificationDataList.isEmpty()){
            throw new UserNotFoundException();
        }
        try{
            Validation.validate(emailLoginVerificationDataList, "emailLoginVerificationDataList",  className);
        }catch(IllegalArgumentException exception){
            logger.error(
                    "Repository sent null in user list while " +
                            "fetching user encrypted email for login verification",
                    exception
            );
            throw new UnexpectedIllegalStateException(
                    "Repository sent null in user list while " +
                            "fetching user encrypted email for login verification",
                    exception
            );
        }

        for(UserEmailProjection emailLoginVerificationData : emailLoginVerificationDataList){
            if(
                    email.equalsIgnoreCase(
                            this.cryptoFacade.decrypt(
                                    EncryptionResultMapper.from(
                                            emailLoginVerificationData
                                    )
                            )
                    )
            ){
                return ;
            }
        }
        throw new UserNotFoundException();
    }

    private void sendOtp(
            String email,
            String token
    ){

        this.emailOtpService.send(
                new EmailOtpData(
                        PURPOSE.name(),
                        email,
                        token,
                        OTP_EXPIRY_SECONDS,
                        OTP_TTL_SECONDS

                )
        );
    }

    private void verifyOtp(
            String identifier,
            String token,
            String otp
    ){
        this.otpService.verifyOtp(
                new VerifyOtpDetails(
                        PURPOSE.name(),
                        identifier,
                        token,
                        otp,
                        OTP_EXPIRY_SECONDS
                )
        );
    }

    private UserDataProjection getVerifiedUser(String email){
        List<HashingResult> hashDataList = this.cryptoFacade.getDetailedHashCandidates(
                email
        );

        List<UserDataProjection> userLoginDataList = this.loginRepositoryPort.getUsersData(
                EmailIndexDataMapper.from(
                        hashDataList
                )
        );
        if(userLoginDataList == null
                || userLoginDataList.isEmpty()){
            logger.error(
                    "User not found after successful OTP verification"
            );
            throw new UserNotFoundAfterVerificationException();
        }

        try{
            Validation.validate(userLoginDataList, "userLoginDataList", className);
        }catch (IllegalArgumentException exception){
            logger.error(
                    "Repository sent null in user list while fetching user data for login",
                    exception
            );
            throw new UnexpectedIllegalStateException(
                    "Repository sent null in user list while fetching user data for login",
                    exception
            );
        }

        for(UserDataProjection userLoginData : userLoginDataList){
            if(
                    email.equalsIgnoreCase(
                            this.cryptoFacade.decrypt(
                                    EncryptionResultMapper.from(
                                            userLoginData
                                    )
                            )
                    )
            ){
                return userLoginData;
            }
        }
        logger.error(
                "User not found after successful OTP verification"
        );
        throw new UserNotFoundAfterVerificationException();

    }

    private void rotateUserDataCrypto(UserDataProjection userLoginData, Map<String, String> plainDataMap){

        UsersRotationData encryptedUserData = this.getLatestEncryptedData(
                userLoginData, plainDataMap
        ).orElse(null);

        UsersLookupRotationData hashedUserData = this.getLatestHashData(
                userLoginData, plainDataMap
        ).orElse(null);

        if(encryptedUserData == null
                && hashedUserData == null ) {
            return;
        }

        this.loginRepositoryPort.rotateUsersCrypto(
                new UsersCryptoRotationData(
                        userLoginData.userLookupId(),
                        encryptedUserData,
                        hashedUserData
                )
        );

    }

    private Optional<UsersRotationData> getLatestEncryptedData(UserDataProjection userLoginData, Map<String, String> plainDataMap){
        boolean isEncryptionLatest = this.cryptoFacade.isLatestEncryption(
                userLoginData.encryptionKeyId(),
                userLoginData.encryptionVersion()
        );

        if(isEncryptionLatest){
            return Optional.empty();
        }

        EncryptionResultMap encryptedData = this.cryptoFacade.encryptDataMap(plainDataMap);

        return Optional.of(
                new UsersRotationData(
                        encryptedData.encryptedDataMap().get(MAP_KEY_PUBLIC_ID),
                        encryptedData.encryptedDataMap().get(MAP_KEY_USERNAME),
                        encryptedData.encryptedDataMap().get(MAP_KEY_EMAIL),
                        encryptedData.keyId(),
                        encryptedData.version()
                )
        );

    }

    private Optional<UsersLookupRotationData> getLatestHashData(UserDataProjection userLoginData, Map<String, String> plainDataMap){

        boolean isHashingLatest = this.cryptoFacade.isLatestHashing(
                userLoginData.hashingKeyId(),
                userLoginData.hashingVersion()
        );
        if(isHashingLatest){
            return Optional.empty();
        }

        HashingResultMap hashData = this.cryptoFacade.hashDataMap(
                plainDataMap
        );

        return Optional.of(new UsersLookupRotationData(
                hashData.hashDataMap().get(MAP_KEY_PUBLIC_ID),
                hashData.hashDataMap().get(MAP_KEY_USERNAME),
                hashData.hashDataMap().get(MAP_KEY_EMAIL),
                hashData.keyId(),
                hashData.version()
        ));
    }
}
