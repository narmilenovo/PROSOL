package com.example.user_management.service;

import static com.example.user_management.utils.Constants.NO_USER_FOUND_WITH_EMAIL_MESSAGE;
import static com.example.user_management.utils.Constants.NO_USER_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.USER_FOUND_WITH_EMAIL_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_management.client.DepartmentResponse;
import com.example.user_management.client.PlantResponse;
import com.example.user_management.client.PlantServiceClient;
import com.example.user_management.client.UserDepartmentPlantResponse;
import com.example.user_management.client.UserDepartmentResponse;
import com.example.user_management.client.UserPlantResponse;
import com.example.user_management.dto.request.UpdatePasswordRequest;
import com.example.user_management.dto.request.UpdateUserRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.request.UserRoleRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.AuditFields;
import com.example.user_management.entity.Role;
import com.example.user_management.entity.User;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.RoleRepository;
import com.example.user_management.repository.UserRepository;
import com.example.user_management.service.interfaces.UserService;
import com.example.user_management.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final ModelMapper modelMapper;
	private final BCryptPasswordEncoder passwordEncoder;
	private final PlantServiceClient plantService;

	@Override
	public UserResponse saveUser(UserRequest userRequest) throws ResourceFoundException {
		List<String> fieldsToSkipCapitalization = Arrays.asList("email", "password", "confirmPassword", "phone");
		Helpers.inputTitleCase(userRequest, fieldsToSkipCapitalization);
		boolean exists = userRepository.existsByEmail(userRequest.getEmail());
		if (!exists) {
			User user = modelMapper.map(userRequest, User.class);
			user.setId(null);
			user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
			user.setRoles(setToRoleId(userRequest.getRoles()));
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
			List<String> fieldsToSkipCapitalization = Arrays.asList("email", "password", "confirmPassword", "phone");
			Helpers.inputTitleCase(userRequest, fieldsToSkipCapitalization);
			boolean exists = userRepository.existsByEmail(userRequest.getEmail());
			if (!exists) {
				User user = modelMapper.map(userRequest, User.class);
				user.setId(null);
				user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
				user.setStatus(false);
				user.setDepartmentId(userRequest.getDepartmentId());
				user.setRoles(setToRoleId(userRequest.getRoles()));
				userList.add(user);
			}
		}
		List<User> savedList = userRepository.saveAll(userList);
		return savedList.stream().map(this::mapToUserResponse).toList();
	}

	@Override
	public UserResponse getUserById(Long id, String show) throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserResponse(user);
	}

	@Override
	public UserPlantResponse getUserPlantById(Long id, String show) throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserPlantResponse(user);
	}

	@Override
	public UserDepartmentResponse getUserDepartmentById(Long id, String show) throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserDepartmentResponse(user);
	}

	@Override
	public UserDepartmentPlantResponse getUserDepartmentPlantById(Long id, String show)
			throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserDepartmentPlantResponse(user);
	}

	@Override
	public List<UserResponse> getAllUsers(String show) {
		List<User> users = userRepository.findAll();
		return users.stream().sorted(Comparator.comparing(User::getId)).map(this::mapToUserResponse).toList();
	}

	@Override
	public List<UserPlantResponse> getAllUserPlants(String show) {
		List<User> users = userRepository.findAll();
		return users.stream().sorted(Comparator.comparing(User::getId)).map(this::mapToUserPlantResponse).toList();
	}

	@Override
	public List<UserDepartmentResponse> getAllUserDepartment(String show) {
		List<User> users = userRepository.findAll();
		return users.stream().sorted(Comparator.comparing(User::getId)).map(this::mapToUserDepartmentResponse).toList();
	}

	@Override
	public List<UserDepartmentPlantResponse> getAllUserDepartmentPlants(String show) {
		List<User> users = userRepository.findAll();
		return users.stream().sorted(Comparator.comparing(User::getId)).map(this::mapToUserDepartmentPlantResponse)
				.toList();
	}

	@Override
	public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) throws ResourceNotFoundException {
		User existingUser = this.findUserById(id);
		List<String> fieldsToSkipCapitalization = Arrays.asList("phone");
		Helpers.inputTitleCase(updateUserRequest, fieldsToSkipCapitalization);

		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();

		if (!existingUser.getFirstName().equals(updateUserRequest.getFirstName())) {
			auditFields.add(
					new AuditFields(null, "First name", existingUser.getFirstName(), updateUserRequest.getFirstName()));
			existingUser.setFirstName(updateUserRequest.getFirstName());
		}
		if (!existingUser.getLastName().equals(updateUserRequest.getLastName())) {
			auditFields.add(
					new AuditFields(null, "Last name", existingUser.getLastName(), updateUserRequest.getLastName()));
			existingUser.setLastName(updateUserRequest.getLastName());

		}
		if (!existingUser.getPhone().equals(updateUserRequest.getPhone())) {
			auditFields.add(new AuditFields(null, "Phone", existingUser.getPhone(), updateUserRequest.getPhone()));
			existingUser.setPhone(updateUserRequest.getPhone());
		}
		if (!existingUser.getBusiness().equals(updateUserRequest.getBusiness())) {
			auditFields.add(
					new AuditFields(null, "Business", existingUser.getBusiness(), updateUserRequest.getBusiness()));
			existingUser.setBusiness(updateUserRequest.getBusiness());
		}
		if (!existingUser.getDepartmentId().equals(updateUserRequest.getDepartmentId())) {
			auditFields.add(new AuditFields(null, "Department", existingUser.getDepartmentId(),
					updateUserRequest.getDepartmentId()));
			existingUser.setDepartmentId(updateUserRequest.getDepartmentId());
		}
		if (!existingUser.getPlantId().equals(updateUserRequest.getPlantId())) {
			auditFields.add(new AuditFields(null, "Plant", existingUser.getPlantId(), updateUserRequest.getPlantId()));
			existingUser.setPlantId(updateUserRequest.getPlantId());
		}
		if (!existingUser.getStatus().equals(updateUserRequest.getStatus())) {
			auditFields.add(new AuditFields(null, "Status", existingUser.getStatus(), updateUserRequest.getStatus()));
			existingUser.setStatus(updateUserRequest.getStatus());
		}
		if (!existingUser.getRoles().equals(setToRoleId(updateUserRequest.getRoles()))) {
			auditFields.add(new AuditFields(null, "Roles", existingUser.getRoles(), updateUserRequest.getRoles()));
			existingUser.setRoles(setToRoleId(updateUserRequest.getRoles()));
		}
		existingUser.updateAuditHistory(auditFields);

		User updateUser = userRepository.save(existingUser);

		return mapToUserResponse(updateUser);
	}

	@Override
	public UserResponse updateStatusUsingId(Long id) throws ResourceNotFoundException {
		User existingUser = this.findUserById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingUser.getStatus() != null) {
			auditFields.add(new AuditFields(null, "Status", existingUser.getStatus(), !existingUser.getStatus()));
			existingUser.setStatus(!existingUser.getStatus());
		}
		existingUser.updateAuditHistory(auditFields);
		User updateUser = userRepository.save(existingUser);
		return mapToUserResponse(updateUser);
	}

	@Override
	public List<UserResponse> updateBulkStatusUsingId(List<Long> ids) throws ResourceNotFoundException {
		List<User> existingUsers = this.findAllUsersById(ids);
		List<AuditFields> auditFields = new ArrayList<>();
		existingUsers.forEach(existingUser -> {
			if (existingUser.getStatus() != null) {
				auditFields.add(new AuditFields(null, "Status", existingUser.getStatus(), !existingUser.getStatus()));
				existingUser.setStatus(!existingUser.getStatus());
			}
			existingUser.updateAuditHistory(auditFields);
		});
		userRepository.saveAll(existingUsers);
		return existingUsers.stream().map(this::mapToUserResponse).toList();
	}

	@Override
	public UserResponse updatePassword(Long id, UpdatePasswordRequest updatePasswordRequest)
			throws ResourceNotFoundException {
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

	@Override
	public void deleteUserId(Long id) throws ResourceNotFoundException {
		User user = this.findUserById(id);
		userRepository.deleteById(user.getId());
	}

	@Override
	public void deleteBatch(List<Long> ids) throws ResourceNotFoundException {
		this.findAllUsersById(ids);
		userRepository.deleteAllByIdInBatch(ids);
	}

	public Set<Role> setToRoleId(Long[] roles) {
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

	private UserDepartmentResponse mapToUserDepartmentResponse(User user) {
		UserDepartmentResponse userDepartmentResponse = modelMapper.map(user, UserDepartmentResponse.class);
		DepartmentResponse department = plantService.getDepartmentById(user.getDepartmentId());
		userDepartmentResponse.setDepartment(department);
		return userDepartmentResponse;
	}

	private UserPlantResponse mapToUserPlantResponse(User user) {
		UserPlantResponse userPlantResponse = modelMapper.map(user, UserPlantResponse.class);
		List<PlantResponse> plants = new ArrayList<>();
		for (Long plantId : user.getPlantId()) {
			PlantResponse plant = plantService.getPlantById(plantId);
			plants.add(plant);
		}
		userPlantResponse.setPlants(plants);
		return userPlantResponse;
	}

	private UserDepartmentPlantResponse mapToUserDepartmentPlantResponse(User user) {

		UserDepartmentPlantResponse userDepartmentPlantResponse = modelMapper.map(user,
				UserDepartmentPlantResponse.class);
		DepartmentResponse department = plantService.getDepartmentById(user.getDepartmentId());
		userDepartmentPlantResponse.setDepartment(department);
		List<PlantResponse> plants = new ArrayList<>();
		for (Long plantId : user.getPlantId()) {
			PlantResponse plant = plantService.getPlantById(plantId);
			plants.add(plant);
		}
		userDepartmentPlantResponse.setPlants(plants);
		return userDepartmentPlantResponse;
	}

	private User findUserById(Long id) throws ResourceNotFoundException {
		Optional<User> user = userRepository.findById(id);
		if (user.isEmpty()) {
			throw new ResourceNotFoundException(NO_USER_FOUND_WITH_ID_MESSAGE);
		}
		return user.get();
	}

	private List<User> findAllUsersById(List<Long> ids) throws ResourceNotFoundException {
		List<User> users = userRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> users.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("User's with IDs " + missingIds + " not found.");
		}
		return users;
	}

	private UserResponse modifyRole(Long userId, UserRoleRequest userRoleRequest, String operation)
			throws ResourceNotFoundException {
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
