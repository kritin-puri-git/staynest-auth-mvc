package com.project.staynest.auth.persistence.db.jpa.adapter;

import com.project.staynest.auth.persistence.db.jpa.mapper.RefreshTokenDataMapper;
import com.project.staynest.auth.persistence.db.jpa.model.RefreshTokenData;
import com.project.staynest.auth.persistence.db.jpa.model.TokenIdentifier;
import com.project.staynest.auth.persistence.db.jpa.repository.JwtRepository;
import com.project.staynest.auth.persistence.db.port.RefreshTokenAuthenticationPort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MysqlRefreshTokenAuthenticationAdapter implements RefreshTokenAuthenticationPort {

    private final JwtRepository jwtRepository;
    public MysqlRefreshTokenAuthenticationAdapter(
            JwtRepository jwtRepository
    ){
        this.jwtRepository = jwtRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RefreshTokenData> getRefreshTokenData(TokenIdentifier tokenIdentifier) {

        return this.jwtRepository.findBySubjectAndSessionId(
                        tokenIdentifier.subject(),
                        tokenIdentifier.sessionId()
                )
                .map(RefreshTokenDataMapper::from);
    }
}
