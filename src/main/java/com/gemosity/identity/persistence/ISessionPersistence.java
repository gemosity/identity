package com.gemosity.identity.persistence;

import com.gemosity.identity.dto.SessionsDTO;

public interface ISessionPersistence {
    SessionsDTO createSession(String userUuid);
}
