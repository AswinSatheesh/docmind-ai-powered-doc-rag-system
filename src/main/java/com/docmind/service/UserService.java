package com.docmind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.docmind.dto.CreateUserRequest;
import com.docmind.dto.UserResponse;
import com.docmind.entity.User;
import com.docmind.mapper.UserMapper;
import com.docmind.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository; //dependency Injection
	
	public UserResponse registerUser(CreateUserRequest request) {
		
		// 1. Translate the incoming web request packet into a raw database Entity row
		User userEntity = UserMapper.toEntity(request);
		
		// 2. Save that row safely to your PostgreSQL database
		User savedUser = userRepository.save(userEntity);
		
		// 3. Translate the saved database row back into a clean, safe Response packet
		return UserMapper.toResponse(savedUser);
	}
	
	public UserResponse getUserById(Long id) {
		User userEntity = userRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("User Not Found by Id : " + id));
		
		return UserMapper.toResponse(userEntity);
	}
}
