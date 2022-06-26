package com.gemosity.user.persistence.couchbase.repository;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.*;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.*;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.gemosity.user.dto.UserDTO;

import com.gemosity.user.persistence.IUserPersistence;
import com.gemosity.user.persistence.couchbase.CouchbaseInstance;
import com.gemosity.user.util.DateUtils;
import com.gemosity.user.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.UUID;

@Component
public class UserProfileRepository implements IUserPersistence {

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
            System.out.println("Open identity bucket collection");

            couchbaseInstance.fetchCluster().buckets().getBucket(bucketName);
            identityBucket = couchbaseInstance.fetchCluster().bucket(bucketName);

        } catch (BucketNotFoundException e) {
            System.out.println("Create identity bucket collection");
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
        System.out.println("Creating users bucket");
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
        System.out.println("Creating users collection");
        CollectionSpec collectionSpec = CollectionSpec.create("users");
        couchbaseInstance.fetchBucket(bucketName).collections().createCollection(collectionSpec);
        return couchbaseInstance.fetchBucket(bucketName).collection(collectionName);
    }

    public UserDTO test() {

        GetResult result = couchbaseInstance.fetchBucket(bucketName).collection("users").get("nick");
        return result.contentAs(UserDTO.class);
    }


    @Override
    public UserDTO createUser(UserDTO userDTO) {
        userDTO.setCreated(Instant.now());
        userDTO.setModified(Instant.now());

        if(userDTO.getUuid() != null) {
            MutationResult mutationResult = usersCollection.insert(userDTO.getUuid(), userDTO);
        } else {
            return null;
        }

        return userDTO;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        userDTO.setModified(Instant.now());
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
