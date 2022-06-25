package com.gemosity.user.persistence.couchbase;

import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.user.util.UuidUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CredentialsRepositoryTests {

    private CredentialRepository credentialRepository;

    @Mock
    private CouchbaseInstance couchbaseInstance;

    @Mock
    private MutationResult mutationResult;

    @Mock
    private Collection credentialCollection;

    @Mock
    Cluster cluster;

    @Mock
    BucketManager bucketManager;

    @Mock
    BucketSettings bucketSettings;

    @Mock
    Bucket bucket;

    @Mock
    private UuidUtil uuidUtil;

    @Mock
    CollectionManager collectionManager;

    @Mock
    Scope scope;

    @Mock
    Collection collection;

    @BeforeEach
    void setUp() {
        credentialRepository = new CredentialRepository(couchbaseInstance, uuidUtil);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createCredentials() {
        String bucketName = "user_credentials";

        MockCouchbaseInstancePostConstruct(bucketName);

        CredentialDTO credentials = new CredentialDTO();
        credentials.setUsername("joe.bloggs");
        credentials.setPassword("password");
        credentials.setActive(true);
        credentials.setDomain("scope");
        credentials.setUuid("uuid");

        Mockito.when(uuidUtil.generateUuid()).thenReturn("uuid");
        Mockito.when(credentialCollection.insert("uuid", credentials)).thenReturn(mutationResult);

        CredentialDTO ret = credentialRepository.createCredentials(credentials);
    }


    private void MockCouchbaseInstancePostConstruct(String bucketName) {
        Mockito.when(couchbaseInstance.fetchCluster()).thenReturn(cluster);
        Mockito.when(cluster.buckets()).thenReturn(bucketManager);
        Mockito.when(bucketManager.getBucket(bucketName)).thenReturn(bucketSettings);
        Mockito.when(cluster.bucket(bucketName)).thenReturn(bucket);
        Mockito.when(couchbaseInstance.fetchBucket(any())).thenReturn(bucket);
        Mockito.when(bucket.collections()).thenReturn(collectionManager);
        Mockito.doThrow(new CollectionExistsException("")).when(collectionManager).createCollection(any());
        Mockito.when(bucket.defaultScope()).thenReturn(scope);
        Mockito.when(scope.collection(any())).thenReturn(credentialCollection);
        credentialRepository.setup();
    }
}

