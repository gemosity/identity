package com.gemosity.user.persistence.couchbase;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.MutationResult;
import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.dto.UserDTO;
import com.gemosity.user.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.user.service.UsernameBasedAuthImpl;
import com.gemosity.user.util.UuidUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class UserProfileRepositoryTests extends CouchbaseInstanceMock  {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UsernameBasedAuthImpl authService;

    @Mock
    private Scope scope;

    @Mock
    private Collection userProfileCollection;

    @Mock
    private UuidUtil uuidUtil;

    @Mock
    private MutationResult mutationResult;


    @BeforeEach
    void setUp() {
        userProfileRepository = new UserProfileRepository(this.getCouchbaseInstance(), uuidUtil);
        setupUserProfileRepository("user_profiles");
    }

    @Test
    void contextLoads() {
    }

    private void setupUserProfileRepository(String bucketName) {
        super.MockCouchbaseInstancePostConstruct(bucketName);

        Mockito.when(scope.collection(any())).thenReturn(userProfileCollection);
        Mockito.when(this.getBucket().defaultScope()).thenReturn(scope);

        // Call Spring @PostConstruct annotation manually
        userProfileRepository.setup();
    }


    @Test
    void createUser() {
        String uuid = "uuid";
        UserDTO userProfile = new UserDTO();
        userProfile.setUuid(uuid);

        Mockito.when(userProfileCollection.insert(uuid, userProfile)).thenReturn(mutationResult);

        UserDTO ret = userProfileRepository.createUser(userProfile);
        Assertions.assertEquals(uuid, ret.getUuid());
    }

    @Test
    void createUserWithNullUuid() {
        UserDTO userProfile = new UserDTO();
        userProfile.setUuid(null);

        UserDTO ret = userProfileRepository.createUser(userProfile);
        Assertions.assertEquals(null, ret);
    }
}
