package com.example.user_management.security;

import static com.example.user_management.utils.Constants.ROLE_PREFIX;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.user_management.entity.Privilege;
import com.example.user_management.entity.Role;
import com.example.user_management.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyUserPrincipal implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final transient User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

		// Navigate through the assignees to get the roles and privileges
		user.getAssignees().forEach(assignee -> {
			Role role = assignee.getRole();
			if (role != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()));
				List<Privilege> privileges = role.getPrivileges();
				if (privileges != null) {
					privileges.forEach(
							privilege -> grantedAuthorities.add(new SimpleGrantedAuthority(privilege.getName())));
				}
			}
		});
		return grantedAuthorities;
	}

	@Override
	@JsonIgnore
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
