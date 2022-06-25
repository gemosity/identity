package com.gemosity.user.persistence;

import com.gemosity.user.dto.SessionsDTO;

public interface ISessionPersistence {
    SessionsDTO createSession(SessionsDTO sessions);
}
