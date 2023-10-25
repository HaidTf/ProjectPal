package com.projectpal.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.request.entity.SprintCreationDto;
import com.projectpal.entity.Sprint;

@Mapper(componentModel = "spring")
public interface SprintMapper {

	@Mapping(source = "nameAndDescriptionAttribute.name", target = "name")
	@Mapping(source = "nameAndDescriptionAttribute.description", target = "description")
	Sprint toSprint(SprintCreationDto sprintCreationDto);
	
}
