package com.docmind.mapper;

import com.docmind.dto.CreateUserRequest;
import com.docmind.dto.UserResponse;
import com.docmind.entity.User;

public class UserMapper {
	//converts the request object to database entity object
	public static User toEntity(CreateUserRequest request) {
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		return user;
	}
	
	//converts database entity object to secure outgoing response object 
	public static UserResponse toResponse(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		//password is completely skipped here
		return response;
	}
}
