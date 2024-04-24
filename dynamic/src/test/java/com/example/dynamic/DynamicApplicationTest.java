package com.example.dynamic;


import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
@TestConfiguration
public class DynamicApplicationTest {
	
	 	@Bean
	    @Primary
	    HttpServletRequest httpServletRequest() {
	        return Mockito.mock(HttpServletRequest.class);
	    }

}
