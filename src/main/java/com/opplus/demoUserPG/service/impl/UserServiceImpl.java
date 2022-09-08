package com.opplus.demoUserPG.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.JsonArray;
import com.opplus.demoUserPG.domain.UserEntity;
import com.opplus.demoUserPG.dto.UserDto;
import com.opplus.demoUserPG.mapper.UserMapper;
import com.opplus.demoUserPG.repository.UserRepository;
import com.opplus.demoUserPG.service.UserService;
import com.opplus.utilidades.Utilidades;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private UserRepository userRepository;

	private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

	@Override
	public List<UserDto> findAll() {
		this.logger.info("Find All Users");
		return userMapper.toDto((List<UserEntity>) this.userRepository.findAll());
	}

	@Override
	public List<UserDto> findAllPrint() {
		this.logger.info("Find All Users and generate pdf");
		List<UserDto> usuarios = userMapper.toDto((List<UserEntity>) this.userRepository.findAll());
		ObjectMapper objMapper = new ObjectMapper();

		try {
			byte[] pdfUsuarios = Utilidades.exportPDF(objMapper.writeValueAsString(usuarios));
			Storage storage = StorageOptions.newBuilder().setProjectId(env.getProperty("spring.cloud.gcp.project-id"))
					.build().getService();
			BlobId blobId = BlobId.of("demouserpg", "usuarios.pdf");
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
			storage.createFrom(blobInfo, new ByteArrayInputStream(pdfUsuarios));

//		} catch (JsonProcessingException e) {
//			
//			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("No se ha podido generar el PDF");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return usuarios;
	}

	@Override
	public UserDto findUserById(Long userId) {
		this.logger.info("Find User By Id {}", userId);
		Optional<UserEntity> result = this.userRepository.findById(userId);
		if (result.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", userId));
		else
			return userMapper.toDto(result.get());
	}

	@Override
	public UserDto createUser(UserDto userDto) {
		this.logger.info("Create User {}", userDto);

		UserEntity newUser = userMapper.toEntity(userDto);
		newUser = this.userRepository.save(newUser);
		this.logger.info("Created User with id {}", newUser.getId());
		return userMapper.toDto(newUser);

	}

	@Override
	public Long deleteUserById(Long userId) {
		this.logger.info("Delete User by userId {}", userId);
		Optional<UserEntity> userOrig = this.userRepository.findById(userId);
		if (userOrig.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", userId));
		else {
			this.logger.info("Deleted User by userId {}", userId);
			this.userRepository.deleteById(userId);
			return Long.valueOf(1);
		}
	}

	@Override
	public UserDto updateUserById(Long userId, UserDto user) {
		this.logger.info("Update User by userId {} with {}", userId, user);

		// Recuperar el user por el id especificado
		Optional<UserEntity> userOrig = this.userRepository.findById(userId);
		if (userOrig.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User %s not found", userId));
		else {
			UserEntity userUpd = userMapper.toEntity(user);
			if (userUpd.getNombre() == null || userUpd.getNombre().isBlank())
				userUpd.setNombre(userOrig.get().getNombre());
			if (userUpd.getApellido1() == null || userUpd.getApellido1().isBlank())
				userUpd.setApellido1(userOrig.get().getApellido1());
			if (userUpd.getApellido2() == null || userUpd.getApellido2().isBlank())
				userUpd.setApellido2(userOrig.get().getApellido2());

			userUpd.setId(userId);
			try {
				return userMapper.toDto(this.userRepository.save(userUpd));
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
						String.format("User %s not updated", userId));
			}

		}

	}

}