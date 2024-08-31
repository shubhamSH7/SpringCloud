package com.micro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class Security {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        	.authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
        	.httpBasic(Customizer.withDefaults());
		
        return http.build();
		
	}
}
