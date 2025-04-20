package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.code_space.code_space_editor.project_managment.dto.file.FileDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.File;

@Mapper(componentModel = "spring")
public interface FileMapper {
    // @Mapping(target = "content", ignore = true)
    FileDTO toDTO(File file);

    // @Mapping(target = "branch", ignore = true)
    // @Mapping(target = "commit", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    // @Mapping(target = "path", ignore = true)
    File toEntity(FileDTO dto);
}
