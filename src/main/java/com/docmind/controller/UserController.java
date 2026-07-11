package com.docmind.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docmind.dto.CreateUserRequest;
import com.docmind.dto.UserResponse;
import com.docmind.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/register")
	public UserResponse registerUser(@RequestBody CreateUserRequest request) {
		return userService.registerUser(request);
	}	
	
	@GetMapping("/{id}")
	public UserResponse getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}
}
