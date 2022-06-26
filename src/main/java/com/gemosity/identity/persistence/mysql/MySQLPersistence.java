package com.gemosity.identity.persistence.mysql;

import com.gemosity.identity.dto.UserDTO;
import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.mysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MySQLPersistence implements IUserPersistence {

    private UserRepository userRepository;

    @Autowired
    public MySQLPersistence(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {

        return null;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserDTO deleteUser(UserDTO userDTO) {
        return null;
    }

}
