package com.docmind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.docmind.dto.CreateUserRequest;
import com.docmind.dto.UpdateUserRequest;
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
	
	//get userByID
	public UserResponse getUserById(Long id) {
		User userEntity = userRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("User Not Found by Id : " + id));
		
		return UserMapper.toResponse(userEntity);
	}
	
	//delete userByID
	public void deleteRecordById(Long id) {
		if(userRepository.existsById(id)) {
			userRepository.deleteById(id);
		}else {
			throw new RuntimeException("Cannot Delete. User not found with id : " + id);
		}
	}
	
	//update userRequest By Id : 
	public UserResponse updateUserReqById(Long id, UpdateUserRequest request) {
		
		//1. Fetch the existing entity record row from PostgreSQL database
		User existingUser = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User With Id Not Found"));
		
		// 2. Overwrite changing attributes from our inbound DTO packet
		existingUser.setEmail(request.getEmail());
		existingUser.setName(request.getName());
		
		// 3. Save the modified row state back down to persistent disk storage
		User updatedUser = userRepository.save(existingUser);
		
		// 4. Return translated safe response blueprint payload back to gateway
		return UserMapper.toResponse(updatedUser);
	}
}
