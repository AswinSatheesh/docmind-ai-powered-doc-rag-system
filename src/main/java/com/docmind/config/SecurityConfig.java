package com.docmind.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	// 1. This bean creates the mathematical hashing engine we'll use to encrypt passwords
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// 2. This bean controls which URLs are locked and which are publicly accessible
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf-> csrf.disable())// Disable CSRF for stateless REST APIs
		.authorizeHttpRequests(auth -> auth
		// Allow absolute public access to Swagger files and our user paths
//		.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/users/**").permitAll()
		.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/users/**").permitAll()
		.anyRequest().authenticated());
		return http.build();
	}
	
}
