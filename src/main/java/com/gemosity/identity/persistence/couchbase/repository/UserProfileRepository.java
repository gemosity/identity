package com.gemosity.identity.persistence.couchbase.repository;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.*;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.*;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.gemosity.identity.dto.UserDTO;

import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.CouchbaseInstance;
import com.gemosity.identity.util.UuidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Component
public class UserProfileRepository implements IUserPersistence {

    private static final Logger log = LogManager.getLogger(UserProfileRepository.class);

    private String bucketName = "user_profiles";
    private String collectionName = "users";

    private UuidUtil uuidUtil;

    private CouchbaseInstance couchbaseInstance;
    private Collection usersCollection;

    @Autowired
    public UserProfileRepository(CouchbaseInstance couchbaseInstance, UuidUtil uuidUtil) {
        this.couchbaseInstance = couchbaseInstance;
        this.uuidUtil = uuidUtil;
    }

    @PostConstruct
    public void setup() {
        Bucket identityBucket;

        try {
            log.info("Open identity bucket collection");

            couchbaseInstance.fetchCluster().buckets().getBucket(bucketName);
            identityBucket = couchbaseInstance.fetchCluster().bucket(bucketName);

        } catch (BucketNotFoundException e) {
            log.info("Creating identity bucket collection");
            e.printStackTrace();
            identityBucket = createIdentityBucket();
        }

        try {
            usersCollection = createUsersCollection();
        } catch (CollectionExistsException e) {
            usersCollection = identityBucket.defaultScope().collection(collectionName);
        }
    }

    private Bucket createIdentityBucket() {
        couchbaseInstance.fetchCluster().buckets()
                .createBucket(BucketSettings.create(bucketName)
                        .bucketType(BucketType.COUCHBASE)
                        .ramQuotaMB(250)
                        .numReplicas(0)
                        .replicaIndexes(true)
                        .flushEnabled(true));

        return couchbaseInstance.fetchBucket(bucketName);

    }

    private Collection createUsersCollection() {
        log.info("Creating users collection");
        CollectionSpec collectionSpec = CollectionSpec.create("users");
        couchbaseInstance.fetchBucket(bucketName).collections().createCollection(collectionSpec);
        return couchbaseInstance.fetchBucket(bucketName).collection(collectionName);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        userDTO.setCreated(Instant.now().getEpochSecond());
        userDTO.setModified(Instant.now().getEpochSecond());

        if(userDTO.getUuid() != null) {
            MutationResult mutationResult = usersCollection.insert(userDTO.getUuid(), userDTO);
        } else {
            return null;
        }

        return userDTO;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        userDTO.setModified(Instant.now().getEpochSecond());
        MutationResult mutationResult = usersCollection.replace(userDTO.getUuid(), userDTO);
        return userDTO;
    }

    @Override
    public UserDTO deleteUser(UserDTO userDTO) {
        MutationResult mutationResult = usersCollection.remove(userDTO.getUuid());
        userDTO.setUuid(null);
        return userDTO;
    }
}
