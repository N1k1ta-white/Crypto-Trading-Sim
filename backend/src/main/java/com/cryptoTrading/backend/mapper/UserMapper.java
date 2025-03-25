package com.cryptoTrading.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cryptoTrading.backend.dto.UserDto;
import com.cryptoTrading.backend.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDto userToDto(User user);

    User dtoToUser(UserDto userDto);
}