package com.project.staynest.auth.persistence.db.jpa.entity;

import com.project.staynest.auth.validation.Validation;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "jwt",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_jwt_refresh_jwt_id",
                        columnNames = "refresh_jwt_id"
                ),
                @UniqueConstraint(
                        name = "uq_jwt_access_jwt_id",
                        columnNames = "access_jwt_id"
                ),
                @UniqueConstraint(
                        name = "uq_jwt_session_id",
                        columnNames = "session_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_jwt_subject",
                        columnList = "subject"
                )
        }
)
public class JwtEntity {

    @Transient
    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false
    )
    private Long id;

    @Column(
            name = "subject",
            nullable = false
    )
    private String subject;

    @Column(
            name = "refresh_jwt_id",
            nullable = false
    )
    private String refreshJwtId;

    @Column(
            name = "access_jwt_id",
            nullable = false
    )
    private String accessJwtId;

    @Column(
            name = "session_id",
            nullable = false
    )
    private String sessionId;

    @Column(
            name = "refresh_expires_at",
            nullable = false
    )
    private Instant refreshExpiresAt;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Column(
            name = "role",
            nullable = false
    )
    private String role;

    @Column(
            name = "device_id",
            nullable = false
    )
    private String deviceId;

    @Column(
            name = "user_agent",
            nullable = false
    )
    private String userAgent;


    public JwtEntity(){}

    public void setSubject(String subject){
        Validation.validate(subject, "subject", CLASS_NAME);

        this.subject = subject;
    }

    public void setRefreshJwtId(String refreshJwtId){
        Validation.validate(refreshJwtId, "refreshJwtId", CLASS_NAME);

        this.refreshJwtId = refreshJwtId;
    }

    public void setAccessJwtId(String accessJwtId){
        Validation.validate(accessJwtId, "accessJwtId", CLASS_NAME);

        this.accessJwtId = accessJwtId;
    }

    public void setSessionId(String sessionId){
        Validation.validate(sessionId, "sessionId", CLASS_NAME);

        this.sessionId = sessionId;
    }

    public void setRefreshExpiresAt(Instant refreshExpiresAt){
        Validation.validate(refreshExpiresAt, "refreshExpiresAt", CLASS_NAME);

        this.refreshExpiresAt = refreshExpiresAt;
    }

    public void setStatus(String status){
        Validation.validate(status, "status", CLASS_NAME);

        this.status = status;
    }

    public void setRole(String role){
        Validation.validate(role, "role", CLASS_NAME);

        this.role = role;
    }

    public void setDeviceId(String deviceId){
        Validation.validate(deviceId, "deviceId", CLASS_NAME);

        this.deviceId = deviceId;
    }

    public void setUserAgent(String userAgent){
        Validation.validate(userAgent, "userAgent", CLASS_NAME);

        this.userAgent = userAgent;
    }


    public String getSubject(){
        return this.subject;
    }

    public String getRefreshJwtId(){
        return this.refreshJwtId;
    }

    public String getAccessJwtId(){
        return this.accessJwtId;
    }

    public String getSessionId(){
        return this.sessionId;
    }

    public Instant getRefreshExpiresAt(){
        return this.refreshExpiresAt;
    }

    public String getStatus(){

        return this.status;
    }

    public String getRole(){
        return this.role;
    }

    public String getDeviceId(){
        return this.deviceId;
    }

    public String getUserAgent(){

        return this.userAgent;
    }

}
