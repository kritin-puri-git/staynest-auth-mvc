package com.project.staynest.auth.persistence.db.jpa.adapter;

import com.project.staynest.auth.persistence.db.jpa.entity.JwtEntity;
import com.project.staynest.auth.persistence.db.jpa.mapper.JwtEntityMapper;
import com.project.staynest.auth.persistence.db.jpa.model.JwtRotationData;
import com.project.staynest.auth.persistence.db.jpa.model.JwtTokenData;
import com.project.staynest.auth.persistence.db.jpa.querydsl.repository.JwtQueryRepository;
import com.project.staynest.auth.persistence.db.jpa.repository.JwtRepository;
import com.project.staynest.auth.persistence.db.port.JwtRepositoryPort;
import org.springframework.stereotype.Repository;

@Repository
public class MysqlJwtRepositoryAdapter implements JwtRepositoryPort {

    private final JwtRepository jwtRepository;
    private final JwtQueryRepository jwtQueryRepository;
    public MysqlJwtRepositoryAdapter(
            JwtRepository jwtRepository,
            JwtQueryRepository jwtQueryRepository
    ){
        this.jwtRepository = jwtRepository;
        this.jwtQueryRepository = jwtQueryRepository;
    }

    @Override
    public void saveJwtData(JwtTokenData jwtTokenData) {
        JwtEntity jwtEntity = JwtEntityMapper.from(
                jwtTokenData
        );

        this.jwtRepository.save(jwtEntity);
    }

    @Override
    public void rotateJwt(JwtRotationData jwtRotationData) {

        this.jwtQueryRepository.rotateJwt(jwtRotationData);
    }


}
