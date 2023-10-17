package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.request.entity.EpicCreationDto;
import com.projectpal.entity.Epic;

@Mapper(componentModel = "spring")
public interface EpicMapper {

	@Mapping(source = "nameAndDescriptionAttribute.name", target = "name")
	@Mapping(source = "nameAndDescriptionAttribute.description", target = "description")
	@Mapping(source = "priorityAttribute.priority", target = "priority")
	Epic toEpic(EpicCreationDto epicCreationDto);

}
