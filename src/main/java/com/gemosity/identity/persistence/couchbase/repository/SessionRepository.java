package com.gemosity.identity.persistence.couchbase.repository;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.gemosity.identity.dto.SessionsDTO;
import com.gemosity.identity.persistence.ISessionPersistence;
import com.gemosity.identity.persistence.couchbase.CouchbaseInstance;
import com.gemosity.identity.util.UuidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SessionRepository implements ISessionPersistence {
    private String bucketName = "user_sessions";
    private String collectionName = "sessions";
    private CouchbaseInstance couchbaseInstance;
    private Collection sessionsCollection;
    private UuidUtil uuidUtil;

    private static final Logger log = LogManager.getLogger(SessionRepository.class);

    @Autowired
    public SessionRepository(CouchbaseInstance couchbaseInstance, UuidUtil uuidUtil) {
        log.info("SessionRepository");
        this.couchbaseInstance = couchbaseInstance;
        this.uuidUtil = uuidUtil;
    }

    @PostConstruct
    public void setup() {
        log.info("setup");

        Bucket credentialsBucket;

        try {
            log.info("Open session bucket collection");

            couchbaseInstance.fetchCluster().buckets().getBucket(bucketName);
            credentialsBucket = couchbaseInstance.fetchCluster().bucket(bucketName);

        } catch (BucketNotFoundException e) {
            log.info("Creating session bucket collection");
            e.printStackTrace();
            credentialsBucket = createSessionsBucket();
        }

        try {
            sessionsCollection = createSessionsCollection();
        } catch (CollectionExistsException e) {
            sessionsCollection = credentialsBucket.defaultScope().collection(collectionName);
        }
    }

    private Bucket createSessionsBucket() {
        couchbaseInstance.fetchCluster().buckets()
                .createBucket(BucketSettings.create(bucketName)
                        .bucketType(BucketType.EPHEMERAL)
                        .ramQuotaMB(250)
                        .numReplicas(0)
                        .replicaIndexes(true)
                        .flushEnabled(true));

        return couchbaseInstance.fetchBucket(bucketName);

    }

    private Collection createSessionsCollection() {
        CollectionSpec collectionSpec = CollectionSpec.create(collectionName);
        couchbaseInstance.fetchBucket(bucketName).collections().createCollection(collectionSpec);
        return couchbaseInstance.fetchBucket(bucketName).collection(collectionName);
    }

    @Override
    public SessionsDTO createSession(String userUuid) {
        MutationResult mutationResult = null;

        if(userUuid != null) {

            SessionsDTO userSession = new SessionsDTO();
            userSession.setSessionUuid(uuidUtil.generateUuid());
            userSession.setUserUuid(userUuid);
            mutationResult = sessionsCollection.insert(userSession.getSessionUuid(), userSession);

            return userSession;
        } else {
            return null;
        }

    }
}
