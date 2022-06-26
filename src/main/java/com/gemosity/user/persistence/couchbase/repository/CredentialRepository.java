package com.gemosity.user.persistence.couchbase.repository;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.persistence.ICredentialsPersistence;
import com.gemosity.user.persistence.couchbase.CouchbaseInstance;
import com.gemosity.user.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Component
public class CredentialRepository implements ICredentialsPersistence {
    private String bucketName = "user_credentials";
    private String collectionName = "credentials";
    private CouchbaseInstance couchbaseInstance;
    private Collection credentialsCollection;
    private UuidUtil uuidUtil;

    @Autowired
    public CredentialRepository(CouchbaseInstance couchbaseInstance, UuidUtil uuidUtil) {
        System.out.println("CredentialRepository");
        this.couchbaseInstance = couchbaseInstance;
        this.uuidUtil = uuidUtil;
    }

    @PostConstruct
    public void setup() {
        System.out.println("setup");

        Bucket credentialsBucket;

        try {
            System.out.println("Open identity bucket collection");

            couchbaseInstance.fetchCluster().buckets().getBucket(bucketName);
            credentialsBucket = couchbaseInstance.fetchCluster().bucket(bucketName);

        } catch (BucketNotFoundException e) {
            System.out.println("Create identity bucket collection");
            e.printStackTrace();
            credentialsBucket = createCredentialsBucket();
        }

        try {
            credentialsCollection = createCredentialsCollection();
        } catch (CollectionExistsException e) {
            credentialsCollection = credentialsBucket.defaultScope().collection(collectionName);
        }
    }

    private Bucket createCredentialsBucket() {
        System.out.println("Creating credentials bucket");
        couchbaseInstance.fetchCluster().buckets()
                .createBucket(BucketSettings.create(bucketName)
                        .bucketType(BucketType.COUCHBASE)
                        .ramQuotaMB(250)
                        .numReplicas(0)
                        .replicaIndexes(true)
                        .flushEnabled(true));

        return couchbaseInstance.fetchBucket(bucketName);

    }

    private Collection createCredentialsCollection() {
        System.out.println("Creating credentials collection");
        CollectionSpec collectionSpec = CollectionSpec.create(collectionName);
        couchbaseInstance.fetchBucket(bucketName).collections().createCollection(collectionSpec);
        return couchbaseInstance.fetchBucket(bucketName).collection(collectionName);
    }

    @Override
    public CredentialDTO createCredentials(CredentialDTO credentialDTO) {
        MutationResult mutationResult = null;
        String uuid = uuidUtil.generateUuid();

        try {
            credentialDTO.setUuid(uuid);
            credentialDTO.setCreated(Instant.now());
            credentialDTO.setModified(Instant.now());
            mutationResult = credentialsCollection.insert(credentialDTO.getUsername(), credentialDTO);
        } catch (DocumentExistsException e) {
            return null;
        }

        return credentialDTO;
    }



}
