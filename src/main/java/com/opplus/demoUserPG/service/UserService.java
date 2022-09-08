package com.opplus.demoUserPG.service;

import java.util.List;

import com.opplus.demoUserPG.dto.UserDto;

public interface UserService {

	List<UserDto> findAll();
	
	List<UserDto> findAllPrint();

	UserDto findUserById(Long userId);

	UserDto createUser(UserDto userDto);

	Long deleteUserById(Long userId);

	UserDto updateUserById(Long userId, UserDto user);
}
