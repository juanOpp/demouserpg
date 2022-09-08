package com.opplus.demoUserPG.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opplus.demoUserPG.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	
	
}

