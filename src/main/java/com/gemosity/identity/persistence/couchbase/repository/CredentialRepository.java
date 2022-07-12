package com.gemosity.identity.persistence.couchbase.repository;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.transactions.TransactionGetResult;
import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.persistence.ICredentialsPersistence;
import com.gemosity.identity.persistence.couchbase.CouchbaseInstance;
import com.gemosity.identity.util.UuidUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Optional;

@Component
public class CredentialRepository implements ICredentialsPersistence {

    private static final Logger log = LogManager.getLogger(CredentialRepository.class);

    private static final String bucketName = "user_credentials";
    private static final String collectionName = "credentials";
    private CouchbaseInstance couchbaseInstance;
    private Collection credentialsCollection;
    private UuidUtil uuidUtil;

    @Autowired
    public CredentialRepository(CouchbaseInstance couchbaseInstance, UuidUtil uuidUtil) {
        log.info("CredentialRepository");
        this.couchbaseInstance = couchbaseInstance;
        this.uuidUtil = uuidUtil;
    }

    @PostConstruct
    public void setup() {
        log.info("setup");

        Bucket credentialsBucket;

        try {
            log.info("Open identity bucket collection");

            couchbaseInstance.fetchCluster().buckets().getBucket(bucketName);
            credentialsBucket = couchbaseInstance.fetchCluster().bucket(bucketName);

        } catch (BucketNotFoundException e) {
            log.info("Creating identity bucket collection");
            credentialsBucket = createCredentialsBucket();
        }

        try {
            credentialsCollection = createCredentialsCollection();
        } catch (CollectionExistsException e) {
            credentialsCollection = credentialsBucket.defaultScope().collection(collectionName);
        }
    }

    private Bucket createCredentialsBucket() {
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
        CollectionSpec collectionSpec = CollectionSpec.create(collectionName);
        couchbaseInstance.fetchBucket(bucketName).collections().createCollection(collectionSpec);
        return couchbaseInstance.fetchBucket(bucketName).collection(collectionName);
    }

    @Override
    public CredentialDTO createCredentials(CredentialDTO credentialDTO) {

        //MutationResult mutationResult = null;
        String uuid = uuidUtil.generateUuid();

        try {
            credentialDTO.setUuid(uuid);
            credentialDTO.setCreated(Instant.now().getEpochSecond());
            credentialDTO.setModified(Instant.now().getEpochSecond());

            couchbaseInstance.fetchCluster().transactions().run((transaction) -> {
                TransactionGetResult transactionGetResult = transaction.insert(credentialsCollection, credentialDTO.getUsername(), credentialDTO);
            });

            // Alternative to transaction since only modifying a single collection, in a single bucket.
            //InsertOptions insertOptions = InsertOptions.insertOptions();
            //insertOptions.durability(DurabilityLevel.PERSIST_TO_MAJORITY);
            //insertOptions.build();
            //mutationResult = credentialsCollection.insert(credentialDTO.getUsername(), credentialDTO, insertOptions);
        } catch (DocumentExistsException e) {
            return null;
        }

        return credentialDTO;
    }

    @Override
    public CredentialDTO updateCredentials(CredentialDTO credentialDTO) {
        MutationResult mutationResult = null;

        try {
            credentialDTO.setModified(Instant.now().getEpochSecond());
            mutationResult = credentialsCollection.replace(credentialDTO.getUsername(), credentialDTO);
        } catch (DocumentExistsException e) {
            return null;
        }

        return credentialDTO;
    }

    @Override
    public Optional<CredentialDTO> findByDomainAndUsername(String domain, String username) {
        Optional<CredentialDTO> credentialDTOOptional  = Optional.empty();

        if(username != null) {
            GetResult result = credentialsCollection.get(username);

            if (result != null) {
                CredentialDTO dto = result.contentAs(CredentialDTO.class);
                credentialDTOOptional = Optional.of(dto);
            }
        }

        return credentialDTOOptional;

    }

}
