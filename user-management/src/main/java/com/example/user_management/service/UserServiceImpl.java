package com.example.user_management.service;

import com.example.user_management.dto.request.UpdatePasswordRequest;
import com.example.user_management.dto.request.UpdateUserRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.request.UserRoleRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Role;
import com.example.user_management.entity.User;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.RoleRepository;
import com.example.user_management.repository.UserRepository;
import com.example.user_management.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.user_management.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponse saveUser(UserRequest userRequest) throws ResourceFoundException {
        boolean exists = userRepository.existsByEmail(userRequest.getEmail());
        if (!exists) {
            User user = modelMapper.map(userRequest, User.class);
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setRoles(setToString(userRequest.getRoles()));
            user.setDepartmentId(userRequest.getDepartmentId());
            User savedUser = userRepository.save(user);
            return mapToUserResponse(savedUser);
        }

        throw new ResourceFoundException(USER_FOUND_WITH_EMAIL_MESSAGE);
    }

    @Override
    public List<UserResponse> saveAllUser(List<UserRequest> userRequests) {
        List<User> userList = new ArrayList<>();
        for (UserRequest userRequest : userRequests) {
            boolean exists = userRepository.existsByEmail(userRequest.getEmail());
            if (!exists) {
                User user = modelMapper.map(userRequest, User.class);
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                user.setStatus(false);
                user.setDepartmentId(userRequest.getDepartmentId());
                user.setRoles(setToString(userRequest.getRoles()));
                userList.add(user);
            }
        }
        List<User> savedList = userRepository.saveAll(userList);
        return savedList.stream().map(this::mapToUserResponse).toList();
    }

    @Override
    @Cacheable("users")
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @Cacheable("users")
    public UserResponse getUserById(Long id) throws ResourceNotFoundException {
        User user = findUserById(id);
        return mapToUserResponse(user);
    }

    @Override
    public void deleteUserId(Long id) throws ResourceNotFoundException {
        User user = this.findUserById(id);
        userRepository.deleteById(user.getId());
    }

    @Override
    public void deleteBatch(List<Long> id) {
        List<User> users = userRepository.findAllById(id);
        userRepository.deleteAll(users);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) throws ResourceNotFoundException {
        User existingUser = this.findUserById(id);
        modelMapper.map(updateUserRequest, existingUser);
        existingUser.setRoles(setToString(updateUserRequest.getRoles()));
        existingUser.setDepartmentId(updateUserRequest.getDepartmentId());
        User updateUser = userRepository.save(existingUser);
        return mapToUserResponse(updateUser);
    }

    @Override
    public UserResponse updateStatusUsingId(Long id) throws ResourceNotFoundException {
        User existingUser = this.findUserById(id);
        existingUser.setStatus(!existingUser.getStatus());
        User updateUser = userRepository.save(existingUser);
        return mapToUserResponse(updateUser);
    }

    @Override
    public List<UserResponse> updateBulkStatusUsingId(List<Long> id) {
        List<User> existingUsers = userRepository.findAllById(id);
        for (User user : existingUsers) {
            user.setStatus(!user.getStatus());
        }
        userRepository.saveAll(existingUsers);
        return existingUsers.stream().map(this::mapToUserResponse).toList();
    }

    @Override
    public UserResponse updatePassword(Long id, UpdatePasswordRequest updatePasswordRequest) throws ResourceNotFoundException {
        User user = findUserById(id);
        if (passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
            User updatedUser = userRepository.save(user);
            return mapToUserResponse(updatedUser);
        }
        return null;
    }

    @Override
    public void updatePassword(Long id, String newPassword) throws ResourceNotFoundException {
        User user = this.findUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserResponse findByEmail(String email) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {

            throw new ResourceNotFoundException(NO_USER_FOUND_WITH_EMAIL_MESSAGE);
        }
        return mapToUserResponse(user.get());
    }


    public Set<Role> setToString(Long[] roles) {
        Set<Role> userRoles = new HashSet<>();
        for (Long roleId : roles) {
            Optional<Role> fetchedPrivilege = roleRepository.findById(roleId);
            fetchedPrivilege.ifPresent(userRoles::add);
        }
        return userRoles;
    }

    @Override
    public UserResponse addRolesToUser(Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException {
        return modifyRole(id, userRoleRequest, "add");
    }

    @Override
    public UserResponse removeRolesFromUser(Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException {
        return modifyRole(id, userRoleRequest, "remove");
    }


    private UserResponse mapToUserResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    private User findUserById(Long id) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(NO_USER_FOUND_WITH_ID_MESSAGE);
        }
        return user.get();
    }

    private UserResponse modifyRole(Long userId, UserRoleRequest userRoleRequest, String operation) throws ResourceNotFoundException {
        User user = this.findUserById(userId);
        Set<Role> existingRoles = user.getRoles();
        for (Long roleId : userRoleRequest.getRoles()) {
            Optional<Role> role = roleRepository.findById(roleId);
            role.ifPresent(p -> {
                if (operation.equals("remove")) {
                    existingRoles.remove(p);
                } else {
                    existingRoles.add(p);
                }
            });
        }
        user.setRoles(existingRoles);
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
}
