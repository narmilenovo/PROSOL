package com.example.user_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
	private final LogoutHandler logoutHandler;
	private final AuthEntryPoint unauthorizedHandler;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(req -> req.anyRequest().permitAll())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
				/*
				 * .rememberMe(remember -> remember.key("imran")
				 * .rememberMeParameter("remember") .rememberMeCookieName("farhan")
				 * .tokenValiditySeconds(7 * 24 * 60 * 60) //
				 * .tokenRepository(persistentTokenRepository()) )
				 */
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout.logoutUrl("/auth/logout").addLogoutHandler(logoutHandler).logoutSuccessHandler(
						(request, response, authentication) -> SecurityContextHolder.clearContext()));

		return http.build();
	}

	/*
	 * @Bean RememberMeServices rememberMeServices(UserDetailsService
	 * userDetailsService) { TokenBasedRememberMeServices.RememberMeTokenAlgorithm
	 * encodingAlgorithm =
	 * TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
	 * TokenBasedRememberMeServices rememberMe = new
	 * TokenBasedRememberMeServices("demo", userDetailsService, encodingAlgorithm);
	 * rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.
	 * RememberMeTokenAlgorithm.MD5); return rememberMe; }
	 */

	/*
	 * @Bean public PersistentTokenRepository persistentTokenRepository() {
	 * JdbcTokenRepositoryImpl tokenRepo = new JdbcTokenRepositoryImpl();
	 * tokenRepo.setDataSource(dataSource); return tokenRepo; }
	 */

}
