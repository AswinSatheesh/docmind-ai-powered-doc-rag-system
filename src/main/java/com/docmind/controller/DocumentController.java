package com.docmind.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.docmind.dto.DocumentResponse;
import com.docmind.service.DocumentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {
	
	private final DocumentService documentService;
	
	//Core upload gate endpoint that accepts form multi-part data binaries
	@PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public DocumentResponse uploadFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam("userId") Long userId) {
		
		return documentService.uploadDocument(file, userId);
	}
	
	// 2. Fetch directory array listing for a given user scope
	@GetMapping("/user/{userId}")
	public List<DocumentResponse> getDocumentsByUser(@PathVariable Long userId){
		return documentService.getDocumentsByUserId(userId);
	}

}
