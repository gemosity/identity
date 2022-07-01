package com.gemosity.identity.service;

import com.gemosity.identity.dto.CredentialDTO;
import com.gemosity.identity.dto.UserProfile;
import com.gemosity.identity.dto.migration.MigratedUserBundle;
import com.gemosity.identity.dto.migration.MigrationResults;
import com.gemosity.identity.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.identity.persistence.couchbase.repository.SessionRepository;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.persistence.mysql.domain.cms_v1.LegacyCmsUser;
import com.gemosity.identity.persistence.mysql.repository.LegacyUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;

@Service
public class MigrationService {

    private final LegacyUsersRepository legacyUsersRepository;
    private final UserProfileRepository userProfileRepository;
    private final CredentialRepository credentialRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public MigrationService(LegacyUsersRepository legacyUsersRepository,
                            CredentialRepository credentialRepository,
                            UserProfileRepository userProfileRepository,
                            SessionRepository sessionRepository) {
        this.legacyUsersRepository = legacyUsersRepository;
        this.credentialRepository = credentialRepository;
        this.userProfileRepository = userProfileRepository;
        this.sessionRepository = sessionRepository;
    }

    public synchronized MigrationResults migrateUsers() {
        MigrationResults migrationResults = new MigrationResults();
        int totalUsersMigrated = 0;

        Iterable<LegacyCmsUser> legacyCmsUserIterable = legacyUsersRepository.findAll();
        Iterator<LegacyCmsUser> legacyCmsUserItr = legacyCmsUserIterable.iterator();

        while(legacyCmsUserItr.hasNext()) {
            MigratedUserBundle migratedUserBundle = migrateUser(legacyCmsUserItr.next());

            if (migratedUserBundle != null) {
                credentialRepository.createCredentials(migratedUserBundle.getCredentials());
                userProfileRepository.createUser(migratedUserBundle.getUserProfile());
                totalUsersMigrated++;
            }
        }

        migrationResults.setTotalUsersMigrated(totalUsersMigrated);

        return migrationResults;
    }

    public MigratedUserBundle migrateUser(LegacyCmsUser legacyCmsUser) {
        MigratedUserBundle migratedUserBundle = null;

        if(legacyCmsUser != null) {
            migratedUserBundle = new MigratedUserBundle();
            migratedUserBundle.setCredentials(migrateCredentials(legacyCmsUser));
            migratedUserBundle.setUserProfile(migrateUserProfile(legacyCmsUser));
        }

        return migratedUserBundle;
    }

    private UserProfile migrateUserProfile(LegacyCmsUser legacyCmsUser) {
        UserProfile userProfile = new UserProfile();

        userProfile.setGiven_name(legacyCmsUser.getUsername());
        userProfile.setFamily_name("");
        userProfile.setUuid(legacyCmsUser.getUuid());

        if(legacyCmsUser.getCreatedDate() == null) {
            userProfile.setCreated_at(new Date().toInstant().getEpochSecond());
        } else {
            userProfile.setCreated_at(legacyCmsUser.getCreatedDate().toInstant().getEpochSecond());
        }

        return userProfile;
    }

    private CredentialDTO migrateCredentials(LegacyCmsUser legacyCmsUser) {
        CredentialDTO credentialDTO = new CredentialDTO();

        credentialDTO.setActive(legacyCmsUser.isActive());
        credentialDTO.setUsername(legacyCmsUser.getUsername());
        credentialDTO.setDomain(legacyCmsUser.getDomain());
        credentialDTO.setPassword(legacyCmsUser.getPassword());
        credentialDTO.setPasswordAlgorithm("argon2");
        credentialDTO.setResetEmailAddress(legacyCmsUser.getResetEmailAddress());
        credentialDTO.setUuid(legacyCmsUser.getUuid());
        credentialDTO.setClientUuid(legacyCmsUser.getClientUuid());
        credentialDTO.setFailedLoginAttempts(legacyCmsUser.getFailedLoginAttempts());

        if(legacyCmsUser.getLastSuccessfulLogin() == null) {
            credentialDTO.setLastSuccessfulLogin(0);
        } else {
            credentialDTO.setLastSuccessfulLogin(legacyCmsUser.getLastSuccessfulLogin().toInstant().getEpochSecond());

        }

        if(legacyCmsUser.getLastSuccessfulLogin() == null) {
            credentialDTO.setLastUnsuccessfulLogin(0);
        } else {
            credentialDTO.setLastUnsuccessfulLogin(legacyCmsUser.getLastSuccessfulLogin().toInstant().getEpochSecond());
        }

        return credentialDTO;
    }
}
