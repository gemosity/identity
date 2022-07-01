package com.gemosity.identity.persistence.couchbase;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.MutationResult;
import com.gemosity.identity.dto.UserProfile;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.util.UuidUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class UserProfileRepositoryTests extends CouchbaseInstanceMock  {

    @Mock
    private UserProfileRepository userProfileRepository;

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
        UserProfile userProfile = new UserProfile();
        userProfile.setUuid(uuid);

        Mockito.when(userProfileCollection.insert(eq(uuid), any())).thenReturn(mutationResult);

        UserProfile ret = userProfileRepository.createUser(userProfile);
        Assertions.assertEquals(uuid, ret.getUuid());
    }

    @Test
    void createUserWithNullUuid() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUuid(null);

        UserProfile ret = userProfileRepository.createUser(userProfile);
        Assertions.assertEquals(null, ret);
    }
}
