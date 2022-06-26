package com.gemosity.user.persistence.couchbase;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.context.ErrorContext;
import com.couchbase.client.core.msg.ResponseStatus;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.MutationResult;
import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.user.util.UuidUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CredentialsRepositoryTests extends CouchbaseInstanceMock {
    String bucketName = "user_credentials";

    private CredentialRepository credentialRepository;

    @Mock
    private Scope scope;

    @Mock
    private Collection credentialCollection;

    @Mock
    private MutationResult mutationResult;

    @Mock
    private UuidUtil uuidUtil;

    @BeforeEach
    void setUp() {
        credentialRepository = new CredentialRepository(this.getCouchbaseInstance(), uuidUtil);
        setupCredentialRepository(bucketName);
    }

    @Test
    void contextLoads() {
    }

    public void setupCredentialRepository(String bucketName) {
        super.MockCouchbaseInstancePostConstruct(bucketName);

        Mockito.when(scope.collection(any())).thenReturn(credentialCollection);
        Mockito.when(this.getBucket().defaultScope()).thenReturn(scope);

        // Call Spring @PostConstruct annotation manually
        credentialRepository.setup();
    }

    @Test
    void createCredentials() {
        CredentialDTO credentials = new CredentialDTO();
        credentials.setUsername("joe.bloggs");
        credentials.setPassword("password");
        credentials.setActive(true);
        credentials.setDomain("scope");
        credentials.setUuid("uuid");

        Mockito.when(uuidUtil.generateUuid()).thenReturn("uuid");
        Mockito.when(credentialCollection.insert("joe.bloggs", credentials)).thenReturn(mutationResult);

        CredentialDTO ret = credentialRepository.createCredentials(credentials);
        Assertions.assertEquals("uuid", ret.getUuid());
    }

    @Test
    void attemptToInsertDuplicateUsername() {
        CredentialDTO credentials = new CredentialDTO();
        credentials.setUsername("dupUsername");
        credentials.setPassword("password");
        credentials.setActive(true);
        credentials.setDomain("scope");
        credentials.setUuid("uuid");

        Mockito.when(uuidUtil.generateUuid()).thenReturn("dup_uuid");
        Mockito.when(credentialCollection.insert("dupUsername", credentials)).thenThrow(new DocumentExistsException(new ErrorContext(ResponseStatus.EXISTS) {
            @Override
            public ResponseStatus responseStatus() {
                return super.responseStatus();
            }
        }));

        CredentialDTO ret = credentialRepository.createCredentials(credentials);
        Assertions.assertEquals(null, ret);
    }


}

