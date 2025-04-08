package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;

import com.code_space.code_space_editor.project_managment.dto.BranchDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Branch;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    BranchDTO toDTO(Branch branch);
    Branch toEntity(BranchDTO dto);
}

