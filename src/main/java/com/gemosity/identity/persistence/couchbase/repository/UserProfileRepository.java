package com.gemosity.identity.persistence.couchbase.repository;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.*;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.*;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.gemosity.identity.dto.UserProfile;

import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.CouchbaseInstance;
import com.gemosity.identity.util.UuidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserProfileRepository implements IUserPersistence {

    private static final Logger log = LogManager.getLogger(UserProfileRepository.class);

    private static final String bucketName = "user_profiles";
    private static final String collectionName = "users";

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
    public UserProfile createUser(UserProfile userDTO) {
        userDTO.setCreated_at(Instant.now().getEpochSecond());
        userDTO.setUpdated_at(Instant.now().getEpochSecond());

        if(userDTO.getUuid() != null) {
            MutationResult mutationResult = usersCollection.insert(userDTO.getUuid(), userDTO);
        } else {
            return null;
        }

        return userDTO;
    }

    @Override
    public UserProfile updateUser(UserProfile userDTO) {
        userDTO.setUpdated_at(Instant.now().getEpochSecond());
        MutationResult mutationResult = usersCollection.replace(userDTO.getUuid(), userDTO);
        return userDTO;
    }

    @Override
    public UserProfile deleteUser(UserProfile userDTO) {
        MutationResult mutationResult = usersCollection.remove(userDTO.getUuid());
        userDTO.setUuid(null);
        return userDTO;
    }

    @Override
    public UserProfile findByUuid(String userUuid) {
        GetResult getResult =  couchbaseInstance.fetchBucket(bucketName).collection(collectionName).get(userUuid);
        return getResult.contentAs(UserProfile.class);
    }

    @Override
    public Map<String, Object> findMapByUuid(String userUuid) {
        GetResult getResult =  couchbaseInstance.fetchBucket(bucketName).collection(collectionName).get(userUuid);
        JsonObject couchbaseJsonObj = getResult.contentAsObject();
        Map<String, Object> userProfileMap = new HashMap<>();

        for(String propertyName: couchbaseJsonObj.getNames()) {
            Object documentNode = couchbaseJsonObj.get(propertyName);

            if(documentNode instanceof com.couchbase.client.java.json.JsonArray) {
                com.couchbase.client.java.json.JsonArray jsonArray
                        = (com.couchbase.client.java.json.JsonArray) documentNode;
                userProfileMap.put(propertyName, jsonArray.toList());

            } else {
                userProfileMap.put(propertyName, documentNode);
            }
        }

        return userProfileMap;
    }
}
