package com.example.user_management.configuration;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CORSFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		log.info("Filtering on...........................................................");
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");

		chain.doFilter(req, res);
	}

	/**
	 * Initializes the filter. This method is intentionally left empty, as the
	 * filter does not require any specific initialization logic.
	 *
	 * @param filterConfig the filter configuration
	 */
	@Override
	public void init(FilterConfig filterConfig) {
		// No initialization logic is required for this filter
	}

	/**
	 * Destroys the filter. This method is intentionally left empty, as the filter
	 * does not require any specific cleanup or destruction logic.
	 */
	@Override
	public void destroy() {
		// No cleanup or destruction logic is required for this filter
	}
}
