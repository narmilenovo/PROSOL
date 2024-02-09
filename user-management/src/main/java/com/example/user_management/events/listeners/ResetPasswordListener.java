package com.example.user_management.events.listeners;

import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.events.OnResetPasswordEvent;
import com.example.user_management.service.interfaces.UserAccountService;
import com.example.user_management.utils.Helpers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResetPasswordListener implements ApplicationListener<OnResetPasswordEvent> {
	private static final String TEMPLATE_NAME = "html/password-reset";
	private static final String MAIL_SUBJECT = "Password Reset";

	private final Environment environment;

	private final UserAccountService userAccountService;

	private final JavaMailSender mailSender;

	private final TemplateEngine htmlTemplateEngine;

	@Override
	public void onApplicationEvent(OnResetPasswordEvent event) {
		this.sendResetPasswordEmail(event);
	}

	private void sendResetPasswordEmail(OnResetPasswordEvent event) {
		UserResponse user = event.getUser();
		String token = Helpers.generateRandomString(12);

//        String resetUrl = environment.getProperty("app.url.password-reset") + "?token=" + token;
//		String resetUrl = event.getResetPassUrl() + "/resetPassword?token=" + token;
		String resetUrl = event.getResetPassUrl() + "/Login/ResetPassword?token=" + token;

		log.info("Click this link to verify Your account: {}", resetUrl);
		String mailFrom = environment.getProperty("email.from");
		String mailFromName = environment.getProperty("mail.from.name", "IMRAN");
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper email;
		try {
			email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			email.setTo(user.getEmail());
			email.setSubject(MAIL_SUBJECT);
			email.setFrom(new InternetAddress(mailFrom, mailFromName));

			final Context ctx = new Context(LocaleContextHolder.getLocale());
			ctx.setVariable("email", user.getEmail());
			ctx.setVariable("name", user.getFirstName() + " " + user.getLastName());
			ctx.setVariable("url", resetUrl);

			final String htmlContent = this.htmlTemplateEngine.process(TEMPLATE_NAME, ctx);

			email.setText(htmlContent, true);

			mailSender.send(mimeMessage);
			userAccountService.save(user, token);

		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}