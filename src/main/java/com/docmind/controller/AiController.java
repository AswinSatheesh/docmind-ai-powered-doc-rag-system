package com.docmind.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docmind.service.AiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
	private final AiService aiService; //dependency Injection
	
	// Expose a dynamic GET endpoint to trigger file summary generation
//	@GetMapping("/summarize/{documentId}")
//	public String getDocumentSummary(@PathVariable Long documentId) {
//		return aiService.summarizeDocument(documentId);
//	}
	
	@GetMapping("/chat/{documentId}")
	public String chatWithDocument(@PathVariable Long documentId, @RequestParam String question, @RequestParam String sessionId) {
		return aiService.chatWithDocument(documentId, question, sessionId);
	}
	
	// 🚀 ENDPOINT 1: Calls the summarized caching logic
	@GetMapping("/summarize/{documentId}")
	public String getDocumentSummary(@PathVariable Long documentId) {
		return aiService.summarizeDocument(documentId);
	}
}
