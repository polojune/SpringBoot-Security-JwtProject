package com.cos.jwtproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.jwtproject.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByUsername(String username);
}
