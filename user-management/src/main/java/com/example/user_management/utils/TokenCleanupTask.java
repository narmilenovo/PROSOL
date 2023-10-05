package com.example.user_management.utils;

import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.service.interfaces.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenCleanupTask {

    private final UserAccountService userAccountService;

    @Scheduled(cron = "0 */2 * * * *") // Run every 2 minutes
    public void cleanupExpiredTokens() {
        long currentTimestamp = new Date().getTime();
        List<UserAccountRequest> expiredUserAccounts = userAccountService.findExpiredTokens(currentTimestamp);
        for (UserAccountRequest userAccount : expiredUserAccounts) {
            userAccountService.delete(userAccount.getId());
        }
    }
}
