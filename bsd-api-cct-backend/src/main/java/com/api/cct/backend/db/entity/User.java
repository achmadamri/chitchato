package com.api.cct.backend.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {
    @Id
    private String uuid;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updateAt;

    private String updatedBy;
    
    private String username;

    private String usernameDanswer;

    private String usernameFonnte;

    private String fastapiusersauth;

    private String status;

    private String type;

    private LocalDateTime expiredAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameDanswer() {
        return usernameDanswer;
    }

    public void setUsernameDanswer(String usernameDanswer) {
        this.usernameDanswer = usernameDanswer;
    }

    public String getUsernameFonnte() {
        return usernameFonnte;
    }

    public void setUsernameFonnte(String usernameFonnte) {
        this.usernameFonnte = usernameFonnte;
    }

    public String getFastapiusersauth() {
        return fastapiusersauth;
    }

    public void setFastapiusersauth(String fastapiusersauth) {
        this.fastapiusersauth = fastapiusersauth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}