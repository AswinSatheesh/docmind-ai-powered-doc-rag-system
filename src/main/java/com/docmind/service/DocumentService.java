package com.docmind.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import lombok.Value;
import lombok.var;

@Service
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final DocumentMapper documentMapper; //We can access either like this Dependency Injection or by declaring that method as static and directly accessing classname.method
	
	private final Path rootLocation;
	
	// This constructor injects the database repository and initializes the file folder storage location
	public DocumentService(DocumentRepository documentRepository, @org.springframework.beans.factory.annotation.Value("${file.upload-dir}") String uploadDir,DocumentMapper documentMapper) {
		this.documentRepository = documentRepository;
		this.rootLocation = Paths.get(uploadDir);
		this.documentMapper = documentMapper;
		
		// Automatically build the target directories on your computer if they don't exist yet
		try {
			Files.createDirectory(this.rootLocation);
		}catch(IOException e) {
			throw new RuntimeException("Could not initialize storage Path : ", e);
		}
	}
	
	public DocumentResponse uploadDocument(MultipartFile file, Long userId) {
		try {
			
			if(file.isEmpty()) {
				throw new RuntimeException("Failed to save empty file");
			}
			
			// 1. Generate a clean, safe filename string target path
			
			String originalFileName = file.getOriginalFilename();
			//"Create the full path where the uploaded file should be stored."
			Path destinationFile = this.rootLocation.resolve(Paths.get(originalFileName)).normalize();
			
			// 2. Physically copy the incoming network file stream bytes onto your local computer storage drive
			try (var inputStream = file.getInputStream()){
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
			
			//create a fresh row entity instance
			Document document = new Document();
			document.setFileName(originalFileName);
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
