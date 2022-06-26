package com.gemosity.user.persistence.couchbase;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.MutationResult;
import com.gemosity.user.dto.SessionsDTO;
import com.gemosity.user.persistence.couchbase.repository.SessionRepository;
import com.gemosity.user.service.UsernameBasedAuthImpl;
import com.gemosity.user.util.UuidUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class SessionRepositoryTests extends CouchbaseInstanceMock {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UsernameBasedAuthImpl authService;

    @Mock
    private Scope scope;

    @Mock
    private Collection sessionCollection;

    @Mock
    private UuidUtil uuidUtil;

    @Mock
    private MutationResult mutationResult;


    @BeforeEach
    void setUp() {
        sessionRepository = new SessionRepository(this.getCouchbaseInstance(), uuidUtil);
        setupSessionRepository("user_sessions");
    }

    @Test
    void contextLoads() {
    }

    private void setupSessionRepository(String bucketName) {
        super.MockCouchbaseInstancePostConstruct(bucketName);

        Mockito.when(scope.collection(any())).thenReturn(sessionCollection);
        Mockito.when(this.getBucket().defaultScope()).thenReturn(scope);

        // Call Spring @PostConstruct annotation manually
        sessionRepository.setup();
    }

    @Test
    void createSession() {
        Mockito.when(uuidUtil.generateUuid()).thenReturn("sessionUuid");
        Mockito.when(sessionCollection.insert(eq("sessionUuid"), any())).thenReturn(mutationResult);

        SessionsDTO session = sessionRepository.createSession("userUuid");
        Assertions.assertEquals("userUuid", session.getUserUuid());
        Assertions.assertNotEquals(null, session.getSessionUuid());
        Assertions.assertNotEquals("", session.getSessionUuid());

    }

    @Test
    void createSessionWithNullUserUuid() {
        Mockito.when(uuidUtil.generateUuid()).thenReturn("sessionUuid");
        Mockito.when(sessionCollection.insert(eq("sessionUuid"), any())).thenReturn(mutationResult);

        SessionsDTO session = sessionRepository.createSession(null);
        Assertions.assertEquals(null, session);

    }

}
