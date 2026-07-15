package com.docmind.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.LoaderHandler;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.docmind.entity.Document;
import com.docmind.repository.DocumentRepository;

@Service
public class AiService {
	private final DocumentRepository documentRepository;
	private final Path rootLocation;
//	private final ChatModel chatModel;
	private final ChatClient chatClient;
	
	// Inject Spring AI's ChatMemory repository (which points to PostgreSQL via JDBC)
	public AiService(DocumentRepository documentRepository,
			ChatClient.Builder chatCilentBuilder,
			ChatMemory chatMemory,
			@Value("${file.upload-dir}") String uploadDir
			) {
		
		this.documentRepository = documentRepository;
		this.rootLocation = Paths.get(uploadDir);
		
		// Build the ChatClient with the MessageChatMemoryAdvisor attached!
		this.chatClient = chatCilentBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
	}
	
	/**
     * Ask a question about a document while maintaining conversation history
     * * @param documentId The document to read
     * @param userQuestion The user's query (e.g. "What is the main topic?")
     * @param sessionId A unique identifier for the conversation session (e.g. "session-123")
     */
	
	public String chatWithDocument(Long documentId,String userQuestion, String sessionId) {
		Document document = documentRepository.findById(documentId)
							.orElseThrow(()-> new RuntimeException("Document record not found with id : " + documentId));
		Path physicalFilePath = this.rootLocation.resolve(document.getFileName()).normalize();
		String fileExtension = getFileExtension(document.getFileName());
		
		try {
			String documentContent;
			if("pdf".equalsIgnoreCase(fileExtension)) {
				documentContent = extractTextFromPdf(physicalFilePath);
			}else {
				documentContent = Files.readString(physicalFilePath);
			}
			
			// System instructions telling the AI how to act and what document to refer to
			String systemInstructions = """
					You are DocMind, an expert document assistant.
					Answer the user's questions using the document text below.
					If the answer is not in the document, use your general knowledge but mention it's not in the file.
					
					DOCUMENT TEXT :
					-------------------------
					%s
					-------------------------
					""".formatted(documentContent);
			
			// Execute the chat request with the context of our sessionId
			return chatClient.prompt()
					.system(systemInstructions)
					.user(userQuestion)
					.advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id",sessionId))
					.call()
					.content();
			
		}catch(IOException e) {
			throw new RuntimeException("Error Reading document file : " +e);
		}
	}
	
	
	/**
     * Simple helper to extract the file extension (e.g., "pdf", "txt")
     * 
     * consider fileName = "report.pdf"
		
		fileName.lastIndexOf(".") -> Finds the position (index) of the very last period character in the string. In "report.pdf", the dot is at index 6
		+ 1 -> Adds 1 to move the pointer past the dot to the first letter of the extension. 6 + 1 = 7, which points to "p".
		fileName.substring(...) -> Grabs everything from that starting index to the end of the string. Starting at index 7 extracts "pdf"
     */
	
	private String getFileExtension(String fileName) {
		if(fileName == null || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}
	
	
	//Helper method to extract text from physical PDF files using Apache PDFBox
	private String extractTextFromPdf (Path filePath) throws IOException {
		// Load the PDF file into memory safely
		try (PDDocument pdDocument = Loader.loadPDF(filePath.toFile())){
			PDFTextStripper pdfStripper = new PDFTextStripper();
			return pdfStripper.getText(pdDocument);// Strips and returns raw text content
		}
	}
}
