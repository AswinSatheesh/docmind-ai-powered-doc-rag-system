package com.docmind.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.LoaderHandler;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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
		
		String fileExtension = getFileExtension(document.getFileName()); //Method to extract file extension
		
		try {
			String documentContent;
			
			// Route processing based on file type
			if("pdf".equalsIgnoreCase(fileExtension)) {
				documentContent = extractTextFromPdf(physicalPath);
			}else {				
				// Extract the text lines out of the file //fallback as txt
				documentContent = Files.readString(physicalPath);
			}
			
			
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
