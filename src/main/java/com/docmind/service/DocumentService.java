package com.docmind.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.docmind.dto.DocumentResponse;
import com.docmind.entity.Document;
import com.docmind.mapper.DocumentMapper;
import com.docmind.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper; //We can access either like this Dependency Injection or by declaring that method as static and directly accessing classname.method
	
	public DocumentResponse uploadDocument(MultipartFile file, Long userId) {
		try {
			
			//create a fresh row entity instance
			Document document = new Document();
			document.setFileName(file.getOriginalFilename());
			document.setFileSize(file.getSize());
			document.setFileType(file.getContentType());
			document.setUploadDate(LocalDateTime.now());
			document.setUserId(userId);
			
			//commit this to PostgreSQL
			Document savedDocument = documentRepository.save(document);
			
			//return mapped translation response
			return documentMapper.toResponse(savedDocument);
		}catch(Exception e) {
			throw new RuntimeException("Could not store the file. Error: "+ e.getMessage());
		}
	}
	
	//Retrieval of list of files uploaded by single user:
	//TraditionalWay
//	public List<DocumentResponse> getDocumentsByUserId(Long userId){
//		List<Document> documents = documentRepository.findByUserId(userId);
//		
//		List<DocumentResponse> responses = new ArrayList<>();
//		
//		for(Document document : documents) {
//			responses.add(documentMapper.toResponse(document));
//		}
//		
//		return responses;
//	}
	
	//stream version : modern approach
	public List<DocumentResponse> getDocumentsByUserId(Long userId){
		return documentRepository.findByUserId(userId)
				.stream()
				.map(document -> documentMapper.toResponse(document)) //Method Reference .map(documentMapper::toResponse)
				.collect(Collectors.toList());
	}
}
