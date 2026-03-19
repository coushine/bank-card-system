package com.bankcards.service;

import com.bankcards.dto.user.UserDTO;
import com.bankcards.dto.user.UserLoginDTO;
import com.bankcards.entity.UserEntity;

public interface UserService {
    UserEntity getUser(long id);
    void deleteUser(long id);
    UserDTO getUserDTO(long id);
    UserDTO getUserDTOByEmail(String email);
    UserDTO registerUser(UserEntity userEntity);
    boolean checkUser(UserLoginDTO user);
}
