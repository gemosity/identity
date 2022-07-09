package com.gemosity.identity.persistence.mysql;

import com.gemosity.identity.dto.UserProfile;
import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.mysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MySQLPersistence implements IUserPersistence {

    private UserRepository userRepository;

    @Autowired
    public MySQLPersistence(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfile createUser(UserProfile userDTO) {

        return null;
    }

    @Override
    public UserProfile updateUser(UserProfile userDTO) {
        return null;
    }

    @Override
    public UserProfile deleteUser(UserProfile userDTO) {
        return null;
    }

    @Override
    public UserProfile findByUuid(String userUuid) {
        return null;
    }

    @Override
    public Map<String, Object> findMapByUuid(String userUuid) {
        return null;
    }

}
