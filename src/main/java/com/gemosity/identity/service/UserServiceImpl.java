package com.gemosity.identity.service;

import com.gemosity.identity.persistence.IUserPersistence;
import com.gemosity.identity.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.identity.dto.LoginCredentials;
import com.gemosity.identity.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private UsernameBasedAuthImpl authService;
    private IUserPersistence userPersistence;

    @Autowired
    public UserServiceImpl(UsernameBasedAuthImpl authService, UserProfileRepository userPersistence) {
        this.authService = authService;
        this.userPersistence = userPersistence;
    }

    @Override
    public UserDTO loginUser(LoginCredentials loginCredentials) {
        //User user = userRepository.findByUsername(loginCredentials.getUsername());
        return new UserDTO();
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        return userPersistence.createUser(userDTO);
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        return null;
    }

    @Override
    public UserDTO deleteUser(UserDTO user) {
        return null;
    }

    @Override
    public UserDTO fetchUser(String username) {

        //return userRepository.findByUsername(username);
        return null;
    }


}
