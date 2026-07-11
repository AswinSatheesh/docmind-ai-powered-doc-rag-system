package com.docmind.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
	private String name;
	private String email;
	private String password; // Required to create the account
}
