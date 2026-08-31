package com.project.staynest.auth.presentation.util;

import com.project.staynest.auth.business.model.SigninData;
import com.project.staynest.auth.presentation.dto.response.SigninResponseDTO;
import com.project.staynest.auth.presentation.dto.response.wrapper.ApiResponseDTO;
import com.project.staynest.auth.presentation.mapper.SigninRespnoseDTOMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

public class SigninResponseUtil {
    private SigninResponseUtil(){}

    public static ResponseEntity<ApiResponseDTO<SigninResponseDTO>> signinApiResponse(
            SigninData signinData,
            HttpStatus status,
            String message
    ){
        ResponseCookie refreshTokenCookie = ResponseCookieUtil.generateRefreshTokenCookie(
                signinData.publicId(),
                signinData.jwtGenerationResult().refreshToken(),
                signinData.jwtGenerationResult().refreshTokenAge()
        );

        ResponseCookie activeUserPublicIdCookie = ResponseCookieUtil.generateActiveUserPublicIdCookie(
                signinData.publicId(),
                signinData.jwtGenerationResult().refreshTokenAge()
        );

        ResponseCookie deviceIdCookie = ResponseCookieUtil.generateDeviceIdCookie(
                signinData.jwtGenerationResult().deviceId()
        );

        SigninResponseDTO signinResponseDTO = SigninRespnoseDTOMapper.from(signinData);

        return ResponseEntity.status(status)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + signinData.jwtGenerationResult().accessToken()
                )
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, activeUserPublicIdCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deviceIdCookie.toString())
                .body(
                        ApiResponseDTO.success(message, signinResponseDTO)
                );
    }
}