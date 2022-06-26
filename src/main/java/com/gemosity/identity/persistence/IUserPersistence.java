package com.gemosity.identity.persistence;

import com.gemosity.identity.dto.UserDTO;

public interface IUserPersistence {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    UserDTO deleteUser(UserDTO userDTO);

}
