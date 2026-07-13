package com.docmind.mapper;

import org.springframework.stereotype.Component;

import com.docmind.dto.DocumentResponse;
import com.docmind.entity.Document;

@Component
public class DocumentMapper {
	public DocumentResponse toResponse(Document request) {
		DocumentResponse myResponse = new DocumentResponse();
		myResponse.setId(request.getId());
		myResponse.setFileName(request.getFileName());
		myResponse.setFileType(request.getFileType());
		myResponse.setFileSize(request.getFileSize());
		myResponse.setUploadDate(request.getUploadDate());
		myResponse.setUserId(request.getUserId());
		
		return myResponse;
	}
}
