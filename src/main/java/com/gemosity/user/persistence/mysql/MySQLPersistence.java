package com.gemosity.user.persistence.mysql;

import com.gemosity.user.dto.UserDTO;
import com.gemosity.user.persistence.IUserPersistence;
import com.gemosity.user.persistence.mysql.repository.UserRepository;
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
