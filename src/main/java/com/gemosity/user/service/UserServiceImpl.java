package com.gemosity.user.service;

import com.gemosity.user.persistence.IUserPersistence;
import com.gemosity.user.persistence.couchbase.repository.UserProfileRepository;
import com.gemosity.user.dto.LoginCredentials;
import com.gemosity.user.dto.UserDTO;
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
