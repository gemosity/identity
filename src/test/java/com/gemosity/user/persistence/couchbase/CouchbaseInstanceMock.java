package com.gemosity.user.persistence.couchbase;

import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import lombok.Getter;
import lombok.Setter;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

@Getter
@Setter
public class CouchbaseInstanceMock {
    @Mock
    private CouchbaseInstance couchbaseInstance;

    @Mock
    private Cluster cluster;

    @Mock
    private BucketManager bucketManager;

    @Mock
    private BucketSettings bucketSettings;

    @Mock
    private Bucket bucket;

    @Mock
    private CollectionManager collectionManager;



    public void MockCouchbaseInstancePostConstruct(String bucketName) {
        Mockito.when(couchbaseInstance.fetchCluster()).thenReturn(cluster);
        Mockito.when(cluster.buckets()).thenReturn(bucketManager);
        Mockito.when(bucketManager.getBucket(bucketName)).thenReturn(bucketSettings);
        Mockito.when(cluster.bucket(bucketName)).thenReturn(bucket);
        Mockito.when(couchbaseInstance.fetchBucket(any())).thenReturn(bucket);
        Mockito.when(bucket.collections()).thenReturn(collectionManager);
        Mockito.doThrow(new CollectionExistsException("")).when(collectionManager).createCollection(any());
    }
}
