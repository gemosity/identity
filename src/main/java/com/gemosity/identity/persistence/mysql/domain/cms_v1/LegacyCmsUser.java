package com.gemosity.identity.persistence.mysql.domain.cms_v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="users")
public class LegacyCmsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    private boolean active;

    @Column(length = 50)
    private String uuid;

    private String domain;
    private String username;
    private java.sql.Timestamp createdDate;
    private java.sql.Timestamp leavingDate;

    private java.sql.Timestamp lastSuccessfulLogin;
    private java.sql.Timestamp lastUnsuccessfulLogin;
    private long failedLoginAttempts;

    @JsonIgnore
    @Column(length = 1024)
    private String password;

    @JsonIgnore
    private String resetEmailAddress;

    @Column(length = 50)
    private String clientUuid;

    private String roles;
}

