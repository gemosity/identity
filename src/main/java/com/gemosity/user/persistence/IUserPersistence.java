package com.gemosity.user.persistence;

import com.gemosity.user.dto.UserDTO;

public interface IUserPersistence {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    UserDTO deleteUser(UserDTO userDTO);

}
