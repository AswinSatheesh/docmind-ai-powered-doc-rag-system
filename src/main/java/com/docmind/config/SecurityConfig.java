package com.docmind.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


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
		http
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.csrf(csrf-> csrf.disable())// Disable CSRF for stateless REST APIs
		.authorizeHttpRequests(auth -> auth
		// Allow absolute public access to Swagger files and our user paths
//		.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/users/**").permitAll()
		.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/users/**","/documents/**","/ai/**").permitAll()
		.anyRequest().authenticated());
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOriginPatterns(List.of("https://localhost:5173")); //react url
		corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
		corsConfiguration.setAllowedHeaders(List.of("Authorization","Content-Type"));
		corsConfiguration.setExposedHeaders(List.of("Authorization"));
		corsConfiguration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
	
}
