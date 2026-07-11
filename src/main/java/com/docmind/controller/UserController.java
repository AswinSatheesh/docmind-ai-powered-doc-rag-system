package com.docmind.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docmind.dto.CreateUserRequest;
import com.docmind.dto.UpdateUserRequest;
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
	
	@DeleteMapping("/{id}")
	public String deleteRecordById(@PathVariable Long id) {
		userService.deleteRecordById(id);
		
		return "User with ID " + id + " has been successfully removed";
	}
	
	@PutMapping("/{id}")
	public UserResponse updateUserRequestById(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
		return userService.updateUserReqById(id,request);
	}
}
