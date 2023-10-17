package com.projectpal.mapper;

import org.mapstruct.Mapper;

import com.projectpal.dto.response.entity.UserResponseDto;
import com.projectpal.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserResponseDto toDto(User user);

}
