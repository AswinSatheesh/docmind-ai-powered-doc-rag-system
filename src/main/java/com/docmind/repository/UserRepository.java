package com.docmind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docmind.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
