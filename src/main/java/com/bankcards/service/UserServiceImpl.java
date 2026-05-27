package com.bankcards.service;

import com.bankcards.dto.user.UserDTO;
import com.bankcards.dto.user.UserLoginDTO;
import com.bankcards.dto.user.UpdateUserProfileDTO;
import com.bankcards.entity.UserEntity;
import com.bankcards.entity.enums.RoleUsers;
import com.bankcards.exception.DuplicateResourceException;
import com.bankcards.exception.IncorrectDataException;
import com.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService{

    private static String buildFullName(UserEntity user) {
        return String.format("%s %s %s",
                user.getLastName() == null ? "" : user.getLastName().trim(),
                user.getFirstName() == null ? "" : user.getFirstName().trim(),
                user.getMiddleName() == null ? "" : user.getMiddleName().trim()).trim();
    }

    private static String generateRandomRussianPhone() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        long suffix = r.nextLong(1_000_000_0000L);
        return String.format("+7%010d", suffix);
    }

    private String ensurePhoneNumber(UserEntity user) {
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            return user.getPhoneNumber();
        }
        user.setPhoneNumber(generateRandomRussianPhone());
        return userRepository.save(user).getPhoneNumber();
    }

    private static String normalizeRussianPhone(String rawPhone) {
        if (rawPhone == null || rawPhone.trim().isEmpty()) {
            throw new IncorrectDataException("Phone number is required");
        }
        String digits = rawPhone.replaceAll("\\D", "");
        if (digits.length() != 11 || !(digits.startsWith("7") || digits.startsWith("8"))) {
            throw new IncorrectDataException("Phone must match Russian format: +7XXXXXXXXXX or 8XXXXXXXXXX");
        }
        return "+7" + digits.substring(1);
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            String phone = ensurePhoneNumber(user);
            return new UserDTO((long) user.getId(), user.getEmail(), buildFullName(user), user.getLastName(), user.getFirstName(), user.getMiddleName(), phone, user.getRole());
        }
        return null;
    }

    @Override
    public UserDTO getUserDTOByEmail(String email) {
        Optional<UserEntity> optional = userRepository.findByEmail(email);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            String phone = ensurePhoneNumber(user);
            return new UserDTO((long) user.getId(), user.getEmail(), buildFullName(user), user.getLastName(), user.getFirstName(), user.getMiddleName(), phone, user.getRole());
        }
        return null;
    }

    @Override
    public UserDTO registerUser(UserEntity userEntity) {
        userEntity.setLastName(userEntity.getLastName().trim());
        userEntity.setFirstName(userEntity.getFirstName().trim());
        userEntity.setMiddleName(userEntity.getMiddleName().trim());
        if (userEntity.getPhoneNumber() == null || userEntity.getPhoneNumber().trim().isEmpty()) {
            userEntity.setPhoneNumber(generateRandomRussianPhone());
        } else {
            userEntity.setPhoneNumber(normalizeRussianPhone(userEntity.getPhoneNumber()));
        }
        if (userEntity.getRole() == null) {
            userEntity.setRole(RoleUsers.USER);
        }
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity saved = userRepository.save(userEntity);
        return new UserDTO(
                saved.getId(),
                saved.getEmail(),
                buildFullName(userEntity),
                saved.getLastName(),
                saved.getFirstName(),
                saved.getMiddleName(),
                saved.getPhoneNumber(),
                saved.getRole());
    }

    @Override
    public boolean checkUser(UserLoginDTO user) {
        boolean present = false;
        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());
        if(userEntity.isPresent()){
            UserEntity userEntity1 = userEntity.get();
            String storedPassword = userEntity1.getPassword();
            if (storedPassword != null && storedPassword.startsWith("$2") && passwordEncoder.matches(user.getPassword(), storedPassword)) {
                present = true;
            } else if (storedPassword != null && storedPassword.equals(user.getPassword())) {
                userEntity1.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(userEntity1);
                present = true;
            }
        }
        return present;
    }

    @Override
    public UserDTO updateUserProfile(long id, UpdateUserProfileDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        String newEmail = dto.getEmail() == null ? "" : dto.getEmail().trim();
        String newLastName = dto.getLastName() == null ? "" : dto.getLastName().trim();
        String newFirstName = dto.getFirstName() == null ? "" : dto.getFirstName().trim();
        String newMiddleName = dto.getMiddleName() == null ? "" : dto.getMiddleName().trim();
        String newPhoneNumber = dto.getPhoneNumber() == null ? "" : dto.getPhoneNumber().trim();

        if (newEmail.isEmpty()) throw new IncorrectDataException("Email is required");
        if (newLastName.isEmpty()) throw new IncorrectDataException("Last name is required");
        if (newFirstName.isEmpty()) throw new IncorrectDataException("First name is required");
        if (newMiddleName.isEmpty()) throw new IncorrectDataException("Middle name is required");
        if (newPhoneNumber.isEmpty()) throw new IncorrectDataException("Phone number is required");

        userRepository.findByEmail(newEmail).ifPresent(existing -> {
            if (existing.getId() != user.getId()) {
                throw new DuplicateResourceException("Email already exist");
            }
        });

        user.setEmail(newEmail);
        user.setLastName(newLastName);
        user.setFirstName(newFirstName);
        user.setMiddleName(newMiddleName);
        user.setPhoneNumber(normalizeRussianPhone(newPhoneNumber));
        UserEntity saved = userRepository.save(user);
        return new UserDTO(saved.getId(), saved.getEmail(), buildFullName(saved), saved.getLastName(), saved.getFirstName(), saved.getMiddleName(), saved.getPhoneNumber(), saved.getRole());
    }
}
