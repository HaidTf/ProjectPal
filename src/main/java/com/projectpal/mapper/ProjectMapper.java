package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.request.entity.ProjectCreationDto;
import com.projectpal.dto.response.entity.ProjectResponseDto;
import com.projectpal.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

	ProjectResponseDto toDto(Project project);

	@Mapping(source = "nameAndDescriptionAttribute.name", target = "name")
	@Mapping(source = "nameAndDescriptionAttribute.description", target = "description")
	Project toProject(ProjectCreationDto projectCreationDto);
}
