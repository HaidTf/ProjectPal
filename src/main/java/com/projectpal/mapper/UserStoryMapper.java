package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.request.entity.UserStoryCreationDto;
import com.projectpal.dto.response.entity.UserStoryResponseDto;
import com.projectpal.entity.UserStory;

@Mapper(componentModel = "spring")
public interface UserStoryMapper {

	UserStoryResponseDto toDto(UserStory userStory);

	@Mapping(source = "nameAndDescriptionAttribute.name", target = "name")
	@Mapping(source = "nameAndDescriptionAttribute.description", target = "description")
	@Mapping(source = "priorityAttribute.priority", target = "priority")
	UserStory toUserStory(UserStoryCreationDto userStoryCreationDto);
}
