package com.gemosity.user.service;

import com.gemosity.user.dto.CredentialDTO;
import com.gemosity.user.dto.SessionsDTO;
import com.gemosity.user.dto.UserDTO;
import com.gemosity.user.dto.migration.MigratedUserBundle;
import com.gemosity.user.dto.migration.MigrationResults;
import com.gemosity.user.persistence.couchbase.repository.CredentialRepository;
import com.gemosity.user.persistence.couchbase.repository.SessionRepository;
import com.gemosity.user.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.user.persistence.mysql.domain.cms_v1.LegacyCmsUser;
import com.gemosity.user.persistence.mysql.repository.LegacyUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

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
                sessionRepository.createSession(migratedUserBundle.getSessions());
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
            migratedUserBundle.setSessions(migrateUserSessions(legacyCmsUser));
        }

        return migratedUserBundle;
    }

    private SessionsDTO migrateUserSessions(LegacyCmsUser legacyCmsUser) {
        SessionsDTO sessionsDTO = new SessionsDTO();
        sessionsDTO.setFailedLoginAttempts(legacyCmsUser.getFailedLoginAttempts());
        sessionsDTO.setLastSuccessfulLogin(legacyCmsUser.getLastSuccessfulLogin().toInstant());
        sessionsDTO.setLastUnsuccessfulLogin(legacyCmsUser.getLastSuccessfulLogin().toInstant());
        return sessionsDTO;
    }

    private UserDTO migrateUserProfile(LegacyCmsUser legacyCmsUser) {
        UserDTO userProfile = new UserDTO();

        userProfile.setFirstName(legacyCmsUser.getUsername());
        userProfile.setLastName("");
        userProfile.setUuid(legacyCmsUser.getUuid());
        userProfile.setCreated(legacyCmsUser.getCreatedDate());

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

        return credentialDTO;
    }
}
