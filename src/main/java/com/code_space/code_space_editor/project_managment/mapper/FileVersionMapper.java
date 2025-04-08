package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;

import com.code_space.code_space_editor.project_managment.dto.FileVersionDTO;
import com.code_space.code_space_editor.project_managment.entity.nosql.FileVersion;

@Mapper(componentModel = "spring")
public interface FileVersionMapper {
    FileVersionDTO toDTO(FileVersion fileVersion);

    FileVersion toEntity(FileVersionDTO dto);
}
