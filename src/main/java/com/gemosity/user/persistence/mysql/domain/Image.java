package com.gemosity.user.persistence.mysql.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name="images")
public class Image {

    // IMAGE MICRO-SERVICE ????
    // Local FS, S3 etc.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long imageId;

    @Column(length = 50)
    private String uuid;

    private String title;
    private String photoUsage;     // Profile photo, something else i.e. diagram etc.
    private String license;
    private String owner_id;
    private String privacy;   // Private, Group, Public

    private Timestamp created;
    private Timestamp modified;

    private boolean approved;
    private String approvedBy;  // UUID

    private boolean optimized;

    // Array of different URL for different resolutions, formats (PNG, JPEG, etc), md5
    private String url;

}

