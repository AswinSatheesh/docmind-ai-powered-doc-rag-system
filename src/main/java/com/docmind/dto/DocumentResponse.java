package com.docmind.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentResponse {
	private Long id;
	private String fileName;
	private String fileType;
	private Long fileSize;
	private LocalDateTime uploadDate;
	private Long userId;
}
