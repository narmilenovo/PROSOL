package com.example.user_management.service;

import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.UserAccount;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.UserAccountRepository;
import com.example.user_management.service.interfaces.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.user_management.utils.Constants.INVALID_TOKEN_MESSAGE;
import static com.example.user_management.utils.Constants.RESOURCE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

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

        // Create a new UserAccount entity and save it
        UserAccount userAccount = modelMapper.map(userAccountRequest, UserAccount.class);
        userAccount = userAccountRepository.save(userAccount);

        // Map the saved UserAccount entity back to UserAccountRequest
        mapToUserAccountRequest(userAccount);
    }

    @Override
    @Cacheable("userAccount")
    public List<UserAccountRequest> findAll() {
        List<UserAccount> userAccounts = userAccountRepository.findAll();
        return userAccounts.stream()
                .sorted(Comparator.comparing(UserAccount::getId))
                .map(this::mapToUserAccountRequest)
                .toList();
    }

    @Override
    public void delete(Long id) {
        userAccountRepository.deleteById(id);
    }

    @Override
    public UserAccountRequest findByToken(String token) throws ResourceNotFoundException {
        Optional<UserAccount> userAccount = userAccountRepository.findByToken(token);

        if (userAccount.isEmpty()) {
            throw new ResourceNotFoundException(INVALID_TOKEN_MESSAGE);
        }

        return mapToUserAccountRequest(userAccount.get());
    }

    @Override
    @Cacheable("userAccount")
    public UserAccountRequest findById(Long id) throws ResourceNotFoundException {
        Optional<UserAccount> userAccount = userAccountRepository.findById(id);

        if (userAccount.isEmpty()) {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }

        return mapToUserAccountRequest(userAccount.get());
    }

    @Override
    @Cacheable("userAccount")
    public List<UserAccountRequest> findExpiredTokens(long currentTimestamp) {
        List<UserAccount> userAccount = userAccountRepository.findExpiredTokens(currentTimestamp);
        return userAccount.stream().map(this::mapToUserAccountRequest).toList();
    }

    private UserAccountRequest mapToUserAccountRequest(UserAccount userAccount) {
        return modelMapper.map(userAccount, UserAccountRequest.class);
    }
}

