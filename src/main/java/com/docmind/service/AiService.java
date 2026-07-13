package com.docmind.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.docmind.entity.Document;
import com.docmind.repository.DocumentRepository;

@Service
public class AiService {
	private final DocumentRepository documentRepository;
	private final Path rootLocation;
	private final ChatModel chatModel;
	
	// Injecting ChatModel lets Spring AI automatically connect to out Gemini Cloud API key!
	public AiService(DocumentRepository documentRepository, ChatModel chatModel, @Value("${file.upload-dir}") String uploadDir) {
		this.documentRepository = documentRepository;
		this.rootLocation = Paths.get(uploadDir);
		this.chatModel = chatModel;
	}
	
	//Read the file content from our local drive and transmit it to gemini for an intelligent summary
	public String summarizeDocument(Long documentId) {
		
		//1.check for this documentId record available or not 
		Document document = documentRepository.findById(documentId)
				.orElseThrow(()-> new RuntimeException("Record not found for Id : " + documentId));
		
		// 2. Point directly to where the physical file rests on your hard drive
		Path physicalPath = rootLocation.resolve(document.getFileName()).normalize();
		
		try {
			// 3. Extract the text lines out of the file
			String documentContent = Files.readString(physicalPath);
			
			// 4. Formulate the precise instruction package for Gemini
			String structuralPrompt = "You are an expert document analyst. Please read the following file" 
									+ "and provide a concise, structured, bullet-point summary:\n\n"
									+ documentContent;
			// 5. Fire it up to the Gemini cloud server and extract the final text response string
			return chatModel.call(structuralPrompt);
			
		}catch(IOException e) {
			throw new RuntimeException("Error occurred while reading physical file from disk storage " + e);
		}
	}
}
