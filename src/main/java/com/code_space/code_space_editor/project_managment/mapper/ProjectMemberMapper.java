package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;

import com.code_space.code_space_editor.project_managment.dto.ProjectMemberDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.ProjectMember;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    ProjectMemberDTO toDTO(ProjectMember projectMember);

    ProjectMember toEntity(ProjectMemberDTO dto);
}
