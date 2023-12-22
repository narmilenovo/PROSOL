package com.example.user_management.security;

import static com.example.user_management.utils.Constants.ROLE_PREFIX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.user_management.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final transient User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		user.getRoles().forEach(role -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()));
			role.getPrivileges()
					.forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));

		});
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getStatus();
	}

	public String getFullName() {
		return user.getFirstName() + " " + user.getLastName();
	}
}
