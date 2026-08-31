package com.project.staynest.auth.business.service.impl;

import com.project.staynest.auth.business.constants.OtpBusinessConstants;
import com.project.staynest.auth.business.model.signup.SignupModel;
import com.project.staynest.auth.business.model.signup.VerifySignupModel;
import com.project.staynest.auth.enums.UserStatus;
import com.project.staynest.auth.business.facade.CryptoFacade;
import com.project.staynest.auth.business.model.SentOtpData;
import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.business.model.signup.*;
import com.project.staynest.auth.business.service.JwtTokenService;
import com.project.staynest.auth.business.service.SignupService;

import com.project.staynest.auth.business.enums.Purpose;
import com.project.staynest.auth.business.constants.SignupBusinessConstants;

import com.project.staynest.auth.crypto.encryption.model.EncryptionResultMap;
import com.project.staynest.auth.crypto.hashing.model.HashingResultMap;
import com.project.staynest.auth.errorhandling.exceptions.business.authentication.SignupCacheExpiredException;
import com.project.staynest.auth.errorhandling.exceptions.unexpected.UnexpectedIllegalStateException;
import com.project.staynest.auth.errorhandling.exceptions.business.validation.EmailAlreadyExistsException;
import com.project.staynest.auth.errorhandling.exceptions.business.validation.UsernameAlreadyExistsException;

import com.project.staynest.auth.otp.model.EmailOtpData;
import com.project.staynest.auth.otp.model.VerifyOtpDetails;
import com.project.staynest.auth.otp.service.EmailOtpService;
import com.project.staynest.auth.otp.service.OtpService;


import com.project.staynest.auth.persistence.cache.port.auth.SignupCachePort;

import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheData;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheKeyData;
import com.project.staynest.auth.persistence.cache.redis.model.signup.SignupCacheSessionData;
import com.project.staynest.auth.persistence.db.jpa.model.UserData;
import com.project.staynest.auth.persistence.db.port.SignupRepositoryPort;
import com.project.staynest.auth.validation.Validation;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SignupServiceImpl implements SignupService {
    private final static String MAP_KEY_USERNAME = "username";
    private final static String MAP_KEY_EMAIL = "identifier";
    private final static String MAP_KEY_PUBLIC_ID = "publicId";
    private final static Purpose PURPOSE = Purpose.SIGNUP;
    private final static int SIGNUP_CACHE_EXPIRY_SECONDS = SignupBusinessConstants.SIGN_UP_CACHE_EXPIRY_SECONDS;
    private final static int SIGNUP_CACHE_TTL_SECONDS = SignupBusinessConstants.SIGN_UP_CACHE_TTL_SECONDS;
    private final static int OTP_EXPIRY_SECONDS = OtpBusinessConstants.OTP_EXPIRY_SECONDS;
    private final static int OTP_TTL_SECONDS = OtpBusinessConstants.OTP_TTL_SECONDS;

    private final static Logger logger = LoggerFactory.getLogger(SignupServiceImpl.class);

    private final String CLASS_NAME = this.getClass().getSimpleName();
    
    private final SignupCachePort signupCachePort;
    private final CryptoFacade cryptoFacade;
    private final EmailOtpService emailOtpService;
    private final OtpService otpService;
    private final SignupRepositoryPort signupRepositoryPort;
    private final JwtTokenService jwtTokenService;
    public SignupServiceImpl(
            SignupCachePort signupCachePort,
            CryptoFacade cryptoFacade,
            EmailOtpService emailOtpService,
            OtpService otpService,
            SignupRepositoryPort signupRepositoryPort,
            JwtTokenService jwtTokenService
    ){
        this.signupCachePort = signupCachePort;
        this.cryptoFacade = cryptoFacade;
        this.emailOtpService = emailOtpService;
        this.otpService = otpService;
        this.signupRepositoryPort = signupRepositoryPort;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public SentOtpData signup(SignupModel signupModel) {

        Validation.validate(signupModel, "signupModel", CLASS_NAME);

        this.checkUserExists(signupModel.email(), signupModel.username());

        String token = UUID.randomUUID().toString();

        this.saveSignupCache(
                signupModel,
                token
        );

        this.sendOtp(
                signupModel.email(),
                token
        );

        return new SentOtpData(
                signupModel.email(),
                token
        );
    }

    @Transactional
    @Override
    public SigninData verifySignup(VerifySignupModel verifySignupModel) {

        Validation.validate(verifySignupModel, "verifySignupModel", CLASS_NAME);

        String identifier = verifySignupModel.email();

        this.verifyOtp(
                identifier,
                verifySignupModel.token(),
                verifySignupModel.otp()

        );

        SignupCacheData signupData = this.getSignupCache(verifySignupModel);

        Validation.validate(signupData, "signupData", CLASS_NAME);

        final Map<String, String> decryptedSignupDataMap = this.cryptoFacade.decryptDataMap(
                Map.of(
                        MAP_KEY_USERNAME, signupData.encryptedUsername(),
                        MAP_KEY_EMAIL, signupData.encryptedEmail()
                ),
                signupData.encryptionKeyId(),
                signupData.encryptionVersion()
        );

        String publicId = this.saveUser(
                decryptedSignupDataMap.get(
                        MAP_KEY_USERNAME
                ),
                decryptedSignupDataMap.get(
                        MAP_KEY_EMAIL
                )
        );

        return new SigninData(
                this.jwtTokenService.generateUserTokenDetails(
                        publicId,
                        UserStatus.ACTIVE,
                        verifySignupModel.deviceId(),
                        verifySignupModel.userAgent()
                ),
                publicId,
                decryptedSignupDataMap.get(MAP_KEY_USERNAME),
                decryptedSignupDataMap.get(MAP_KEY_EMAIL)
        );
    }

    private void checkUserExists(String email, String username){
        checkEmailExists(email);
        checkUsernameExists(username);
    }

    private void checkEmailExists(String email){

        Validation.validate(email, "identifier", CLASS_NAME);
        boolean emailExists = this.signupRepositoryPort.checkEmailIndexExists(
                this.cryptoFacade.getHashCandidates(email)
        );
        if(emailExists){
            throw new EmailAlreadyExistsException();
        }
    }

    private void checkUsernameExists(String username){
        Validation.validate(username, "username", CLASS_NAME);
        boolean usernameExists = this.signupRepositoryPort.checkUsernameIndexExists(
                this.cryptoFacade.getHashCandidates(username)
        );
        if(usernameExists){
            throw new UsernameAlreadyExistsException();
        }
    }

    private void saveSignupCache(
            SignupModel signupModel,
            String token
    ){
        Map<String, String> signupData = Map.of(
                MAP_KEY_USERNAME, signupModel.username(),
                MAP_KEY_EMAIL, signupModel.email()
        );
        EncryptionResultMap encryptedDataMap = this.cryptoFacade.encryptDataMap(
                signupData
        );

        byte[] identifierHash = this.cryptoFacade.hashValue(signupModel.email());
        String hashedIdentifier = Base64.getEncoder()
                .withoutPadding()
                .encodeToString(identifierHash);


        SignupCacheSessionData signupCacheSessionData = new SignupCacheSessionData(
                hashedIdentifier,
                token,
                encryptedDataMap.encryptedDataMap().get(MAP_KEY_USERNAME),
                encryptedDataMap.encryptedDataMap().get(MAP_KEY_EMAIL),
                encryptedDataMap.keyId(),
                encryptedDataMap.version(),
                SIGNUP_CACHE_TTL_SECONDS
        );
        this.signupCachePort.saveSignupCache(
                signupCacheSessionData
        );
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

    private SignupCacheData getSignupCache(
            VerifySignupModel verifySignupModel
    ){

        byte[] identifierHash = this.cryptoFacade.hashValue(verifySignupModel.email());
        String hashedIdentifier = Base64.getEncoder()
                .withoutPadding()
                .encodeToString(identifierHash);

        SignupCacheKeyData signupCacheKeyData = new SignupCacheKeyData(
                hashedIdentifier,
                verifySignupModel.token()
        );

        SignupCacheData signupData = this.signupCachePort.getSignupData(
                signupCacheKeyData
        );
        try{
            Validation.validate(signupData, "signupData", CLASS_NAME);
        }catch(IllegalArgumentException exception){
            logger.error(
                    "Signup cache missing after successful OTP verification. " +
                            "Possible Redis anomaly or premature cache expiration.",
                    exception
            );
            throw new SignupCacheExpiredException();
        }
        return signupData;
    }

    private String getPublicId(){

        String publicId = null;
        for(short i=0; i<5; i++){
            String pId = UUID.randomUUID().toString();

            boolean publicIdExists = this.signupRepositoryPort.checkPublicIdIndexExists(
                    this.cryptoFacade.getHashCandidates(pId)
            );
            if(!publicIdExists){
                publicId = pId;
                break;
            }
        }
        if(publicId == null){
            logger.warn(
                    "Unable to generate unique Public Id. " +
                            "5 Different Public Ids(UUID) generated but every id already existed"
            );
            throw new UnexpectedIllegalStateException(
                    "Unable to generate unique Public Id"
            );
        }

        return publicId;
    }

    private String saveUser(final String decryptedUsername, final String decryptedEmail){

        this.checkUserExists(decryptedEmail, decryptedUsername);

        final String publicId = this.getPublicId();

        final Map<String, String> signupDataMap = Map.of(
                MAP_KEY_PUBLIC_ID, publicId,
                MAP_KEY_USERNAME, decryptedUsername,
                MAP_KEY_EMAIL, decryptedEmail
        );
        final EncryptionResultMap encryptedDataMap = this.cryptoFacade.encryptDataMap(
                signupDataMap
        );

        final HashingResultMap hashDataMap = this.cryptoFacade.hashDataMap(
                signupDataMap
        );

        final UserData userData = new UserData(
                encryptedDataMap.encryptedDataMap().get(MAP_KEY_PUBLIC_ID),
                encryptedDataMap.encryptedDataMap().get(MAP_KEY_USERNAME),
                encryptedDataMap.encryptedDataMap().get(MAP_KEY_EMAIL),
                encryptedDataMap.keyId(),
                encryptedDataMap.version(),
                hashDataMap.hashDataMap().get(MAP_KEY_PUBLIC_ID),
                hashDataMap.hashDataMap().get(MAP_KEY_USERNAME),
                hashDataMap.hashDataMap().get(MAP_KEY_EMAIL),
                hashDataMap.keyId(),
                hashDataMap.version(),
                UserStatus.ACTIVE.name()

        );

        this.signupRepositoryPort.saveUserData(
                userData
        );

        return publicId;
    }

}
