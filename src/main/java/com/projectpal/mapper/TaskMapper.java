package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.request.entity.TaskCreationDto;
import com.projectpal.dto.response.entity.TaskResponseDto;
import com.projectpal.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

	TaskResponseDto toDto(Task task);

	@Mapping(source = "nameAndDescriptionAttribute.name", target = "name")
	@Mapping(source = "nameAndDescriptionAttribute.description", target = "description")
	@Mapping(source = "priorityAttribute.priority", target = "priority")
	Task toTask(TaskCreationDto taskCreationDto);

}
