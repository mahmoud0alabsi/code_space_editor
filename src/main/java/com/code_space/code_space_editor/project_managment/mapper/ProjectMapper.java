package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;

import com.code_space.code_space_editor.project_managment.dto.project.ProjectDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO toDTO(Project project);

    Project toEntity(ProjectDTO dto);
}
