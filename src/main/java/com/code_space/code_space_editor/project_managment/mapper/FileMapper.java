package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;

import com.code_space.code_space_editor.project_managment.dto.FileDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.File;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileDTO toDTO(File file);

    File toEntity(FileDTO dto);
}
