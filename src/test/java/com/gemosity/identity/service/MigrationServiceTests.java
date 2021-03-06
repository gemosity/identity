package com.gemosity.identity.service;

import com.gemosity.identity.dto.migration.MigrationResults;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.UserProfile;
import com.gemosity.identity.dto.migration.MigratedUserBundle;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.identity.persistence.couchbase.repository.SessionRepository;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.persistence.mysql.domain.cms_v1.LegacyCmsUser;
import com.gemosity.identity.persistence.mysql.repository.LegacyUsersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class MigrationServiceTests {

    private MigrationService migrationService;

    @Mock
    private LegacyUsersRepository legacyUsersRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        migrationService = new MigrationService(legacyUsersRepository,
                credentialRepository,
                userProfileRepository,
                sessionRepository);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void migrateLegacyUser() {
        LegacyCmsUser legacyCmsUser = generateLegacyUser("username");
        MigratedUserBundle migratedUserBundle = migrationService.migrateUser(legacyCmsUser);

        Assertions.assertEquals(true, verifyCredentials(migratedUserBundle, legacyCmsUser));
        Assertions.assertEquals(true, verifyUserProfile(migratedUserBundle, legacyCmsUser));
    }

    @Test
    void migrateAllLegacyUsers() {
        CredentialDTO credentials = new CredentialDTO();
        credentials.setUuid(UUID.randomUUID().toString());
        List<LegacyCmsUser> legacyCmsUserList = new ArrayList<>();
        legacyCmsUserList.add(generateLegacyUser("admin"));
        legacyCmsUserList.add(generateLegacyUser("test"));
        Mockito.when(legacyUsersRepository.findAll()).thenReturn(legacyCmsUserList);
        Mockito.when(credentialRepository.createCredentials(any())).thenReturn(credentials);

        MigrationResults migrationResult = migrationService.migrateUsers();

        Assertions.assertEquals(legacyCmsUserList.size(), migrationResult.getTotalUsersMigrated());
    }


    private boolean verifyUserProfile(MigratedUserBundle migratedUserBundle, LegacyCmsUser legacyCmsUser) {
        CredentialDTO credentials = migratedUserBundle.getCredentials();
        UserProfile userProfile = migratedUserBundle.getUserProfile();

        if(userProfile.getUuid().contentEquals(credentials.getUuid())
               && userProfile.getCreated_at() == legacyCmsUser.getCreatedDate().toInstant().getEpochSecond()) {
            return true;
        }

        return false;
    }

    private boolean verifyCredentials(MigratedUserBundle migratedUserBundle, LegacyCmsUser legacyCmsUser) {
        CredentialDTO credentials = migratedUserBundle.getCredentials();

        if(credentials.getDomain().contentEquals(legacyCmsUser.getDomain())
                && credentials.getPassword().contentEquals(legacyCmsUser.getPassword())
                && credentials.getPasswordAlgorithm().contentEquals("argon2")
                && credentials.getResetEmailAddress().contentEquals(legacyCmsUser.getResetEmailAddress())
                && credentials.getUuid().contentEquals(legacyCmsUser.getUuid())) {
            return true;
        }

        return false;
    }

    private LegacyCmsUser generateLegacyUser(String userName) {
        LegacyCmsUser legacyUser = new LegacyCmsUser();
        legacyUser.setUserId(0);
        legacyUser.setActive(true);
        legacyUser.setUuid(UUID.randomUUID().toString());
        legacyUser.setUsername(userName);
        legacyUser.setPassword("");
        legacyUser.setClientUuid(UUID.randomUUID().toString());
        legacyUser.setDomain("default");
        legacyUser.setFailedLoginAttempts(0);
        legacyUser.setLastSuccessfulLogin(new Timestamp(0));
        legacyUser.setLastUnsuccessfulLogin(new Timestamp(0));
        legacyUser.setResetEmailAddress("test@test.com");
        legacyUser.setCreatedDate(new Timestamp(0));
        legacyUser.setLeavingDate(new Timestamp(0));
        legacyUser.setRoles("");

        return legacyUser;
    }
}
