package com.project.staynest.auth.persistence.db.jpa.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Arrays;

@Entity
@Table(
        name = "users"
)
public class UsersEntity {

    @Id
    @Column(
            name = "lookup_id",
            nullable = false
    )
    private Long userLookupId;

    @OneToOne(
            cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY
    )
    @MapsId
    @JoinColumn(
            name = "lookup_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_users_lookup_id"
            )
    )
    private UsersLookupEntity usersLookupEntity;

    @Column(
            name = "public_id",
            nullable = false
    )
    private byte[] publicId;

    @Column(
            name = "username",
            nullable = false
    )
    private byte[] username;

    @Column(
            name = "email",
            nullable = false
    )
    private byte[] email;

    @Column(
            name = "encryption_version",
            nullable = false
    )
    private Short encryptionVersion;

    @Column(
            name = "encryption_key_id",
            nullable = false
    )
    private Short encryptionKeyId;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    protected UsersEntity(){}


    public void setUsersLookupEntity(UsersLookupEntity usersLookupEntity){
        if(usersLookupEntity == null){
            throw new IllegalArgumentException(
                    "UsersLookupEntity cannot be null in UsersEntity"
            );
        }
        this.usersLookupEntity = usersLookupEntity;
    }

    public void setPublicId(byte[] publicId) {
        if(publicId == null){
            throw new IllegalArgumentException(
                    "Public Id cannot be null in UsersEntity"
            );
        }
        this.publicId = Arrays.copyOf(
                publicId,
                publicId.length
        );
    }

    public void setUsername(byte[] username) {
        if(username == null){
            throw new IllegalArgumentException(
                    "Username cannot be null in UsersEntity"
            );
        }
        this.username = Arrays.copyOf(
                username,
                username.length
        );
    }

    public void setEmail(byte[] email){
        if(email == null){
            throw new IllegalArgumentException(
                    "Email cannot be null in UsersEntity"
            );
        }
        this.email = Arrays.copyOf(
                email,
                email.length
        );
    }

    public void setEncryptionVersion(short encryptionVersion){
        if(encryptionVersion <= 0){
            throw new IllegalArgumentException(
                    "Encryption version must be positive in UsersEntity"
            );
        }
        this.encryptionVersion = encryptionVersion;
    }

    public void setEncryptionKeyId(short encryptionKeyId){
        if(encryptionKeyId <= 0){
            throw new IllegalArgumentException(
                    "Encryption key userLookupId must be positive in UsersEntity"
            );
        }
        this.encryptionKeyId = encryptionKeyId;
    }

    public void setStatus(String status){
        if(status == null || status.isBlank()){
            throw new IllegalArgumentException(
                    "UserStatus cannot be null or blank in UsersEntity"
            );
        }
        this.status = status.trim();
    }

    public byte[] getPublicId(){

        return this.publicId == null ? null : Arrays.copyOf(
                this.publicId,
                this.publicId.length
        );

    }

    public byte[] getUsername(){
        return this.username == null ? null : Arrays.copyOf(
                this.username,
                this.username.length
        );
    }

    public byte[] getEmail(){
        return this.email == null ? null : Arrays.copyOf(
                this.email,
                this.email.length
        );
    }

    public Short getEncryptionVersion(){
        return this.encryptionVersion;
    }

    public Short getEncryptionKeyId(){
        return this.encryptionKeyId;
    }

    public String getStatus(){
        return this.status;
    }

    public Instant getCreatedAt(){
        return this.createdAt;
    }

    public Instant getUpdatedAt(){
        return this.updatedAt;
    }

    public static class Builder{
        private byte[] publicId;
        private byte[] username;
        private byte[] email;
        private short encryptionVersion;
        private short encryptionKeyId;
        private String status;


        public Builder publicId(byte[] publicId){
            if(publicId == null){
                throw new IllegalArgumentException(
                        "Public Id cannot be null in UsersEntity Builder"
                );
            }
            this.publicId = Arrays.copyOf(
                    publicId,
                    publicId.length
            );
            return this;
        }

        public Builder username(byte[] username){
            if(username == null){
                throw new IllegalArgumentException(
                        "Username cannot be null in UsersEntity Builder"
                );
            }
            this.username = Arrays.copyOf(
                    username,
                    username.length
            );
            return this;
        }

        public Builder email(byte[] email){
            if(email == null){
                throw new IllegalArgumentException(
                        "Email cannot be null in UsersEntity Builder"
                );
            }
            this.email = Arrays.copyOf(
                    email,
                    email.length
            );
            return this;
        }

        public Builder encryptionVersion(short encryptionVersion){
            if(encryptionVersion <= 0){
                throw new IllegalArgumentException(
                        "Encryption version must be positive in UsersEntity Builder"
                );
            }
            this.encryptionVersion = encryptionVersion;
            return this;
        }

        public Builder encryptionKeyId(short encryptionKeyId){
            if(encryptionKeyId <= 0){
                throw new IllegalArgumentException(
                        "Encryption key userLookupId must be positive in UsersEntity Builder"
                );
            }
            this.encryptionKeyId = encryptionKeyId;
            return this;
        }

        public Builder status(String status){
            if(status == null || status.isBlank()){
                throw new IllegalArgumentException(
                        "UserStatus cannot be null or blank in UsersEntity Builder"
                );
            }
            this.status = status.trim();
            return this;
        }

        public UsersEntity build(){
            if(this.publicId == null){
                throw new IllegalArgumentException(
                        "Public Id cannot be null in UsersEntity Builder"
                );
            }
            if(this.username == null){
                throw new IllegalArgumentException(
                        "Username cannot be null in UsersEntity Builder"
                );
            }
            if(this.email == null){
                throw new IllegalArgumentException(
                        "Email cannot be null in UsersEntity Builder"
                );
            }
            if(this.encryptionKeyId <= 0){
                throw new IllegalArgumentException(
                        "Encryption key userLookupId must be positive in UsersEntity Builder"
                );
            }
            if(this.encryptionVersion <= 0){
                throw new IllegalArgumentException(
                        "Encryption version must be positive in UsersEntity Builder"
                );
            }
            if(this.status == null || this.status.isBlank()){
                throw new IllegalArgumentException(
                        "UserStatus cannot be null or blank in UsersEntity"
                );
            }
            UsersEntity usersEntity = new UsersEntity();

            usersEntity.setPublicId(this.publicId);
            usersEntity.setUsername(this.username);
            usersEntity.setEmail(this.email);
            usersEntity.setEncryptionKeyId(this.encryptionKeyId);
            usersEntity.setEncryptionVersion(this.encryptionVersion);
            usersEntity.setStatus(this.status);

            return usersEntity;
        }
    }
    public static Builder builder(){
        return new Builder();
    }

}