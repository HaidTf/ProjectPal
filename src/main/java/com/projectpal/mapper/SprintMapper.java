package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.request.entity.SprintCreationDto;
import com.projectpal.dto.response.entity.SprintResponseDto;
import com.projectpal.entity.Sprint;

@Mapper(componentModel = "spring")
public interface SprintMapper {

	SprintResponseDto toDto(Sprint sprint);
	
	@Mapping(source = "nameAndDescriptionAttribute.name", target = "name")
	@Mapping(source = "nameAndDescriptionAttribute.description", target = "description")
	Sprint toSprint(SprintCreationDto sprintCreationDto);
	
}
