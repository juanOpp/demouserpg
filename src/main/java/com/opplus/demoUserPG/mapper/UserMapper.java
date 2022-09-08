package com.opplus.demoUserPG.mapper;

import org.mapstruct.Mapper;

import com.opplus.demoUserPG.domain.UserEntity;
import com.opplus.demoUserPG.dto.UserDto;


@Mapper(componentModel = "spring", uses = {})
public interface UserMapper extends EntityMapper<UserDto, UserEntity>{


}