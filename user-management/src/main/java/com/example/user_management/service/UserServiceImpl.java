package com.example.user_management.service;

import static com.example.user_management.utils.Constants.NO_USER_FOUND_WITH_EMAIL_MESSAGE;
import static com.example.user_management.utils.Constants.NO_USER_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.USER_FOUND_WITH_EMAIL_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.user_management.client.UserDepartmentPlantResponse;
import com.example.user_management.client.UserDepartmentResponse;
import com.example.user_management.client.UserPlantResponse;
import com.example.user_management.client.plant.DepartmentResponse;
import com.example.user_management.client.plant.PlantResponse;
import com.example.user_management.client.plant.PlantServiceClient;
import com.example.user_management.dto.request.AssigneeRequest;
import com.example.user_management.dto.request.UpdatePasswordRequest;
import com.example.user_management.dto.request.UpdateUserRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.request.UserRoleRequest;
import com.example.user_management.dto.response.RoleUserResponse;
import com.example.user_management.dto.response.UpdateAssigneeResponse;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Assignee;
import com.example.user_management.entity.AuditFields;
import com.example.user_management.entity.Role;
import com.example.user_management.entity.User;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.mapping.AssigneeMapper;
import com.example.user_management.mapping.UserMapper;
import com.example.user_management.repository.AssigneeRepository;
import com.example.user_management.repository.RoleRepository;
import com.example.user_management.repository.UserRepository;
import com.example.user_management.service.interfaces.UserService;
import com.example.user_management.utils.FileUploadUtil;
import com.example.user_management.utils.Helpers;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final AssigneeRepository assigneeRepository;
	private final UserMapper userMapper;
	private final AssigneeMapper assigneeMapper;
	private final BCryptPasswordEncoder passwordEncoder;
	private final PlantServiceClient plantService;
	private final FileUploadUtil fileUploadUtil;

	@Override
	@Transactional
	public UserResponse saveUser(UserRequest userRequest) throws ResourceFoundException, ResourceNotFoundException {
		List<String> fieldsToSkipCapitalization = Arrays.asList("email", "password", "confirmPassword", "phone");
		Helpers.inputTitleCase(userRequest, fieldsToSkipCapitalization);
		String email = userRequest.getEmail();
		if (userRepository.existsByEmail(email)) {
			throw new ResourceFoundException(USER_FOUND_WITH_EMAIL_MESSAGE);
		}
		User user = userMapper.mapToUser(userRequest);
		user.setId(null);
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setAvatar(null);
		user.setDepartmentId(userRequest.getDepartmentId());
		user.setAssignees(setAssigneesToRequests(userRequest.getAssignees()));
		User savedUser = userRepository.save(user);
		return userMapper.mapToUserResponse(savedUser);
	}

	@Override
	@Transactional
	public List<UserResponse> saveAllUser(List<UserRequest> userRequests) {
		List<User> userList = userRequests.stream()
				.filter(userRequest -> !userRepository.existsByEmail(userRequest.getEmail())).map(userRequest -> {
					List<String> fieldsToSkipCapitalization = Arrays.asList("email", "password", "confirmPassword",
							"phone");
					Helpers.inputTitleCase(userRequest, fieldsToSkipCapitalization);
					User user = userMapper.mapToUser(userRequest);
					user.setId(null);
					user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
					user.setAvatar(null);
					user.setStatus(false);
					user.setDepartmentId(userRequest.getDepartmentId());
                    user.setAssignees(setAssigneesToRequests(userRequest.getAssignees()));
                    return user;
				}).toList();

		List<User> savedList = userRepository.saveAll(userList);
		return savedList.stream().map(userMapper::mapToUserResponse).toList();
	}

	@Override
	public UserResponse getUserById(@NonNull Long id, String show) throws ResourceNotFoundException {
		User user = findUserById(id);
		return userMapper.mapToUserResponse(user);
	}

	@Override
	public UserPlantResponse getUserPlantById(@NonNull Long id, String show) throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserPlantResponse(user);
	}

	@Override
	public UserDepartmentResponse getUserDepartmentById(@NonNull Long id, String show)
			throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserDepartmentResponse(user);
	}

	@Override
	public UserDepartmentPlantResponse getUserDepartmentPlantById(@NonNull Long id, String show)
			throws ResourceNotFoundException {
		User user = findUserById(id);
		return mapToUserDepartmentPlantResponse(user);
	}

	@Override
	public List<UserResponse> getAllUsers(String show) {
		return userRepository.findAll().stream().sorted(Comparator.comparing(User::getId))
				.map(userMapper::mapToUserResponse).toList();
	}

	@Override
	public List<UserPlantResponse> getAllUserPlants(String show) {
		return userRepository.findAll().stream().sorted(Comparator.comparing(User::getId))
				.map(this::mapToUserPlantResponse).toList();
	}

	@Override
	public List<UserDepartmentResponse> getAllUserDepartment(String show) {
		return userRepository.findAll().stream().sorted(Comparator.comparing(User::getId))
				.map(this::mapToUserDepartmentResponse).toList();
	}

	@Override
	public List<UserDepartmentPlantResponse> getAllUserDepartmentPlants(String show) {
		return userRepository.findAll().stream().sorted(Comparator.comparing(User::getId))
				.map(this::mapToUserDepartmentPlantResponse).toList();
	}

	@Override
	public List<UserResponse> getAllUsersByPlantId(String show, List<Long> plantIds) {
		return userRepository.findByPlantId(plantIds).stream().sorted(Comparator.comparing(User::getId))
				.map(userMapper::mapToUserResponse).toList();
	}

	@Override
	public List<UserPlantResponse> getAllUserPlantsByPlantId(String show, List<Long> plantIds) {
		return userRepository.findByPlantId(plantIds).stream().sorted(Comparator.comparing(User::getId))
				.map(this::mapToUserPlantResponse).toList();
	}

	@Override
	public List<UserDepartmentResponse> getAllUserDepartmentByPlantId(String show, List<Long> plantIds) {
		return userRepository.findByPlantId(plantIds).stream().sorted(Comparator.comparing(User::getId))
				.map(this::mapToUserDepartmentResponse).toList();
	}

	@Override
	public List<UserDepartmentPlantResponse> getAllUserDepartmentPlantsByPlantId(String show, List<Long> plantIds) {
		return userRepository.findByPlantId(plantIds).stream().sorted(Comparator.comparing(User::getId))
				.map(this::mapToUserDepartmentPlantResponse).toList();
	}

	@Override
	public List<RoleUserResponse> getAllUsersByRoleId(Long id) {
		return userRepository.findByAssignees_Role_Id(id).stream().sorted(Comparator.comparing(User::getId))
				.map(userMapper::mapToRoleUserResponse).toList();
	}

	public List<RoleUserResponse> getAllUsersByPlantIdsAndSubRole(List<Long> plantIds, String subRole) {
		return userRepository.findByPlantIdInAndAssignees_Role_SubRole_Name(plantIds, subRole).stream()
				.sorted(Comparator.comparing(User::getId)).map(userMapper::mapToRoleUserResponse).toList();
	}

	@Override
	@Transactional
	public UserResponse updateUser(@NonNull Long id, UpdateUserRequest updateUserRequest)
			throws ResourceNotFoundException, JsonProcessingException {
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
			auditFields.add(new AuditFields(null, "Plant", existingUser.getPlantId().toString(),
					updateUserRequest.getPlantId().toString()));
			existingUser.setPlantId(updateUserRequest.getPlantId());
		}
		if (!existingUser.getStatus().equals(updateUserRequest.getStatus())) {
			auditFields.add(new AuditFields(null, "Status", existingUser.getStatus(), updateUserRequest.getStatus()));
			existingUser.setStatus(updateUserRequest.getStatus());
		}

		List<Assignee> existingAssignees = existingUser.getAssignees();
		List<UpdateAssigneeResponse> existingAssigneeNames = existingAssignees.stream()
				.map(assigneeMapper::assigneeToUpdateAssigneeResponse).toList();
		String existingJson = Helpers.convertJsonToString(existingAssigneeNames);

		List<Assignee> requestingAssignees = this.setAssigneesToRequests(updateUserRequest.getAssignees());
		List<UpdateAssigneeResponse> requestingAssigneeNames = requestingAssignees.stream()
				.map(assigneeMapper::assigneeToUpdateAssigneeResponse).toList();
		String requestingJson = Helpers.convertJsonToString(requestingAssigneeNames);

		if (!existingAssigneeNames.equals(requestingAssigneeNames)) {
			auditFields.add(new AuditFields(null, "Role Of User", existingJson, requestingJson));

			assigneeRepository.deleteAll(existingAssignees);
			existingUser.setAssignees(requestingAssignees);
		}
		existingUser.updateAuditHistory(auditFields);

		User updateUser = userRepository.save(existingUser);

		return userMapper.mapToUserResponse(updateUser);
	}

	@Override
	public UserResponse updateStatusUsingId(@NonNull Long id) throws ResourceNotFoundException {
		User existingUser = this.findUserById(id);
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingUser.getStatus() != null) {
			auditFields.add(new AuditFields(null, "Status", existingUser.getStatus(), !existingUser.getStatus()));
			existingUser.setStatus(!existingUser.getStatus());
		}
		existingUser.updateAuditHistory(auditFields);
		User updateUser = userRepository.save(existingUser);
		return userMapper.mapToUserResponse(updateUser);
	}

	@Override
	public List<UserResponse> updateBulkStatusUsingId(@NonNull List<Long> ids) throws ResourceNotFoundException {
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
		return existingUsers.stream().map(userMapper::mapToUserResponse).toList();
	}

	@Override
	public UserResponse updatePassword(@NonNull Long id, UpdatePasswordRequest updatePasswordRequest)
			throws ResourceNotFoundException {
		User existingUser = findUserById(id);
		if (passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), existingUser.getPassword())) {
			existingUser.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
			User updatedUser = userRepository.save(existingUser);
			return userMapper.mapToUserResponse(updatedUser);
		}
		return null;
	}

	@Override
	public void updatePassword(@NonNull Long id, String newPassword) throws ResourceNotFoundException {
		User existingUser = this.findUserById(id);
		existingUser.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(existingUser);
	}

	@Override
	public UserResponse uploadProfilePic(Long id, MultipartFile file, String action) throws ResourceNotFoundException {
		User existingUser = findUserById(id);
		String existingImage = existingUser.getAvatar();

		// Handle file upload logic
		String newImage = null;
		if ("u".equalsIgnoreCase(action)) {
			// Delete existing image if it exists
			if (existingImage != null) {
				fileUploadUtil.deleteFile(existingImage, id);
			}
			// Store new image
			newImage = fileUploadUtil.storeFile(file, id);
			existingUser.setAvatar(newImage);
		} else if ("d".equalsIgnoreCase(action)) {
			// Delete existing image if it exists
			if (existingImage != null) {
				fileUploadUtil.deleteFile(existingImage, id);
				existingUser.setAvatar(null);
			}
		} else {
			throw new IllegalArgumentException("Invalid action specified");
		}

		// Save user entity once with updated avatar
		userRepository.save(existingUser);

		return userMapper.mapToUserResponse(existingUser);
	}

	@Override
	public UserResponse findByEmail(String email) throws ResourceNotFoundException {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isEmpty()) {

			throw new ResourceNotFoundException(NO_USER_FOUND_WITH_EMAIL_MESSAGE);
		}
		return userMapper.mapToUserResponse(user.get());
	}

	@Override
	public void deleteUserId(@NonNull Long id) throws ResourceNotFoundException {
		User user = this.findUserById(id);
        if (user.getAvatar() != null) {
            fileUploadUtil.deleteDir(user.getAvatar(), id);
        }
        userRepository.delete(user);
    }

	@Override
	public void deleteBatch(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<User> users = this.findAllUsersById(ids);
		users.forEach(user -> {
			userRepository.delete(user);
			fileUploadUtil.deleteDir(user.getAvatar(), user.getId());
		});
	}

	public List<Role> setToRoleId(Long[] roles) {
		List<Role> userRoles = new ArrayList<>();
		for (Long roleId : roles) {
			if (roleId != null) {
				Optional<Role> fetchedRole = roleRepository.findById(roleId);
				fetchedRole.ifPresent(userRoles::add);
			}
		}
		return userRoles;
	}

	private Assignee createAssignee(AssigneeRequest assigneeRequest) {
		Assignee assignee = new Assignee();
		assignee.setRole(roleRepository.findById(assigneeRequest.getRole()).orElse(null));
		assignee.setSubUser(userRepository.searchById(assigneeRequest.getSubUser()).orElse(null));
		return assignee;
	}

	private List<Assignee> setAssigneesToRequests(List<AssigneeRequest> assigneeRequests) {
		List<Assignee> userAssignees = new ArrayList<>();
		for (AssigneeRequest assigneeRequest : assigneeRequests) {
			Assignee existingAssignee = assigneeRepository.findByRole_IdAndSubUser_Id(assigneeRequest.getRole(),
					assigneeRequest.getSubUser());

			if (existingAssignee == null) {
				Assignee assignee = createAssignee(assigneeRequest);
				assigneeRepository.save(assignee);
				userAssignees.add(assignee);
			} else {
				userAssignees.add(existingAssignee);

			}
		}
		return userAssignees;
	}

	private UserDepartmentResponse mapToUserDepartmentResponse(User user) {
		UserDepartmentResponse userDepartmentResponse = userMapper.mapToUserDepartmentResponse(user);
		DepartmentResponse department = plantService.getDepartmentById(user.getDepartmentId());
		userDepartmentResponse.setDepartment(department);
		return userDepartmentResponse;
	}

	private UserPlantResponse mapToUserPlantResponse(User user) {
		UserPlantResponse userPlantResponse = userMapper.mapToUserPlantResponse(user);
		List<PlantResponse> plants = new ArrayList<>();
		for (Long plantId : user.getPlantId()) {
			PlantResponse plant = plantService.getPlantById(plantId);
			plants.add(plant);
		}
		userPlantResponse.setPlants(plants);
		return userPlantResponse;
	}

	private UserDepartmentPlantResponse mapToUserDepartmentPlantResponse(User user) {

		UserDepartmentPlantResponse userDepartmentPlantResponse = userMapper.mapToUserDepartmentPlantResponse(user);
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

	private User findUserById(@NonNull Long id) throws ResourceNotFoundException {
		Optional<User> user = this.optionalUser(id);
		if (user.isEmpty()) {
			throw new ResourceNotFoundException(NO_USER_FOUND_WITH_ID_MESSAGE);
		}
		return user.get();
	}

	private Optional<User> optionalUser(Long id) {
		return userRepository.findById(id);
	}

	private List<User> findAllUsersById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<User> users = userRepository.findAllById(ids);
		List<Long> missingIds = ids.stream().filter(id -> users.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("User's with IDs " + missingIds + " not found.");
		}
		return users;
	}


	@Override
	public UserResponse unassignUsersFromRole(Long roleId, Long[] userIds) throws ResourceNotFoundException {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

		for (Long userId : userIds) {
			User user = this.findUserById(userId);
			Assignee assignee = assigneeRepository.findByRole(role);
			if (user.getAssignees().contains(assignee)) {
				user.getAssignees().remove(assignee);
			}
			userRepository.save(user);
		}
		return userMapper.mapToUserResponse(findUserById(userIds[0]));

	}

}
