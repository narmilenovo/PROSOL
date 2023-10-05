package com.example.user_management.events;

import com.example.user_management.dto.response.UserResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnResetPasswordEvent extends ApplicationEvent {
    private UserResponse user;
    private String resetPassUrl;

    public OnResetPasswordEvent(UserResponse user, String resetPassUrl) {
        super(user);
        this.user = user;
        this.resetPassUrl = resetPassUrl;
    }

}
