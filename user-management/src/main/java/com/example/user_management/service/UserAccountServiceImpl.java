package com.example.user_management.service;

import static com.example.user_management.utils.Constants.INVALID_TOKEN_MESSAGE;
import static com.example.user_management.utils.Constants.RESOURCE_NOT_FOUND_MESSAGE;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.UserAccount;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.mapping.UserMapper;
import com.example.user_management.repository.UserAccountRepository;
import com.example.user_management.service.interfaces.UserAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

	private final UserAccountRepository userAccountRepository;
	private final UserMapper userMapper;

	@Override
	public void save(UserResponse user, String token) {
		UserAccountRequest userAccountRequest = new UserAccountRequest();
		Date dateNow = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dateNow);
		c.add(Calendar.MINUTE, 2);
		userAccountRequest.setUser(user);
		userAccountRequest.setToken(token);
		userAccountRequest.setExpireAt(c.getTime().getTime());

		UserAccount userAccount = userMapper.mapToUserAccount(userAccountRequest);
		if (userAccount != null) {
			userAccount = userAccountRepository.save(userAccount);
		}
		userMapper.mapToUserAccountRequest(userAccount);
	}

	@Override
	public List<UserAccountRequest> findAll() {
		List<UserAccount> userAccounts = userAccountRepository.findAll();
		return userAccounts.stream().sorted(Comparator.comparing(UserAccount::getId))
				.map(userMapper::mapToUserAccountRequest).toList();
	}

	@Override
	public void deleteById(@NonNull Long id) {
		userAccountRepository.deleteById(id);
	}

	@Override
	public UserAccountRequest findByToken(String token) throws ResourceNotFoundException {
		Optional<UserAccount> userAccount = userAccountRepository.findByToken(token);

		if (userAccount.isEmpty()) {
			throw new ResourceNotFoundException(INVALID_TOKEN_MESSAGE);
		}

		return userMapper.mapToUserAccountRequest(userAccount.get());
	}

	@Override
	public UserAccountRequest findById(@NonNull Long id) throws ResourceNotFoundException {
		Optional<UserAccount> userAccount = userAccountRepository.findById(id);

		if (userAccount.isEmpty()) {
			throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
		}

		return userMapper.mapToUserAccountRequest(userAccount.get());
	}

	@Override
	public List<UserAccountRequest> findExpiredTokens(long currentTimestamp) {
		List<UserAccount> userAccount = userAccountRepository.findExpiredTokens(currentTimestamp);
		return userAccount.stream().map(userMapper::mapToUserAccountRequest).toList();
	}
}
