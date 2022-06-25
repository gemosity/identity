package com.gemosity.user.persistence.mysql.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;

    @Column(length = 50)
    private String uuid;

    private String username;

    @Column(length = 50)
    private String emailUuid;

    private Timestamp created;
    private Timestamp modified;

    private boolean activated;

}
