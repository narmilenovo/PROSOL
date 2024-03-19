package com.example.user_management.utils;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.service.interfaces.UserAccountService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenCleanupTask {

	private final UserAccountService userAccountService;

	@Scheduled(cron = "0 */5 * * * *") // Run every 5 minutes
	public void cleanupExpiredTokens() {
		long currentTimestamp = new Date().getTime();
		List<UserAccountRequest> expiredUserAccounts = userAccountService.findExpiredTokens(currentTimestamp);
		for (UserAccountRequest userAccount : expiredUserAccounts) {
			userAccountService.deleteById(userAccount.getId());
		}
	}
}
