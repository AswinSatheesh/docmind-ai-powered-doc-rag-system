package com.docmind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.docmind.entity.Document;

public interface DocumentRepository extends JpaRepository<Document,Long>{//pass classname and primary key type
	// This allows us to fetch all documents belonging to a specific user.
	List<Document> findByUserId(Long userId);
}
