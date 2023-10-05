package com.example.user_management.service;

import com.example.user_management.entity.User;
import com.example.user_management.repository.UserRepository;
import com.example.user_management.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(this::validateUser).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }

    private UserDetails validateUser(User user) {
        if (user.getStatus().equals(false)) {
            throw new BadCredentialsException("User is Not Enabled");
        }
        return new CustomUserDetails(user);
    }
}
