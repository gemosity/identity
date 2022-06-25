package com.gemosity.user.persistence.couchbase.repository;

import com.gemosity.user.dto.SessionsDTO;
import com.gemosity.user.persistence.ISessionPersistence;
import com.gemosity.user.persistence.couchbase.CouchbaseInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionRepository implements ISessionPersistence {
    private String bucketName = "user_sessions";
    private CouchbaseInstance couchbaseInstance;

    @Autowired
    public SessionRepository(CouchbaseInstance couchbaseInstance) {
        this.couchbaseInstance = couchbaseInstance;
    }

    @Override
    public SessionsDTO createSession(SessionsDTO sessions) {
        return null;
    }
}
