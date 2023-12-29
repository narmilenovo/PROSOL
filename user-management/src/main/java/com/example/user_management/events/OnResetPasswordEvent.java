package com.example.user_management.events;

import org.springframework.context.ApplicationEvent;

import com.example.user_management.dto.response.UserResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnResetPasswordEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	private transient UserResponse user;
	private String resetPassUrl;

	public OnResetPasswordEvent(UserResponse user, String resetPassUrl) {
		super(user);
		this.user = user;
		this.resetPassUrl = resetPassUrl;
	}

}
