package com.project.staynest.auth.security.model;

public record RefreshContext(
        String refreshToken,
        String deviceId,
        String userAgent
) {
}
