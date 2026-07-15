package com.docmind.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String fileName;
	
	@Column(nullable = false)
	private String fileType;
	
	@Column(nullable = false)
	private Long fileSize;
	
	@Column(nullable = false)
	private LocalDateTime uploadDate;
	
	@Column(nullable = false)
	private Long userId;
	
	@Column(columnDefinition = "TEXT") //Instead of standard varchar(255) this one is unlimited on String fields
	private String Summary;
}
