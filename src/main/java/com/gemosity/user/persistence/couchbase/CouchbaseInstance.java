package com.gemosity.user.persistence.couchbase;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class CouchbaseInstance {
    private static final Logger log = LogManager.getLogger(CouchbaseInstance.class);

    @Value("${couchbase.username}")
    private String username;

    @Value("${couchbase.password}")
    private String password;

    @Value("${couchbase.connection}")
    private String connectionString;

    @Value("${couchbase.bucketName}")
    private String bucketName;

    private Cluster cluster = null;

    @PostConstruct
    private void setup() {
        try {
            if (cluster == null) {
                cluster = connectToCluster();
                log.info("Successfully established connection to Couchbase");
            }
        } catch (CouchbaseException e) {
            log.error("Unable to connect to Couchbase", e);
            System.exit(0);
        }
    }

    @PreDestroy
    private void disconnect() {
        // Disconnect cluster
        if (cluster != null) {
            cluster.disconnect();
        }
    }

    private synchronized Cluster connectToCluster() {
        return Cluster.connect(connectionString, username, password);
    }

    public Bucket fetchBucket(String bucketName) throws BucketNotFoundException {
        if (cluster == null) {
            log.error("Couchbase cluster is null");
            cluster = connectToCluster();
        }

        return cluster.bucket(bucketName);
    }

    public Cluster fetchCluster() {
        return cluster;
    }
}
