package com.bankcards.service;

import com.bankcards.dto.user.UserDTO;
import com.bankcards.dto.user.UserLoginDTO;
import com.bankcards.entity.UserEntity;
import com.bankcards.exception.DuplicateResourceException;
import com.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserEntity getUser(long id) {
        UserEntity userEntity = null;
        Optional<UserEntity> optional =  userRepository.findById(id);
        if(optional.isPresent()){
            userEntity = optional.get();
        }
        return userEntity;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserDTO(long id) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            return new UserDTO((long) user.getId(), user.getEmail(), user.getFullName(), user.getRole());
        }
        return null;
    }

    @Override
    public UserDTO getUserDTOByEmail(String email) {
        Optional<UserEntity> optional = userRepository.findByEmail(email);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            return new UserDTO((long) user.getId(), user.getEmail(), user.getFullName(), user.getRole());
        }
        return null;
    }

    @Override
    public UserDTO registerUser(UserEntity userEntity) {
        if(userRepository.findByEmail(userEntity.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exist");
        }
        UserEntity saved = userRepository.save(userEntity);
        return new UserDTO(saved.getId(), saved.getEmail(), saved.getFullName(), saved.getRole());
    }

    @Override
    public boolean checkUser(UserLoginDTO user) {
        boolean present = false;
        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());
        if(userEntity.isPresent()){
            UserEntity userEntity1 = userEntity.get();
            if(userEntity1.getPassword().equals(user.getPassword()))
                present = true;
        }
        return present;
    }
}
