package com.code_space.code_space_editor.project_managment.mapper;

import org.mapstruct.Mapper;

import com.code_space.code_space_editor.project_managment.dto.commit.CommitDTO;
import com.code_space.code_space_editor.project_managment.entity.sql.Commit;

@Mapper(componentModel = "spring")
public interface CommitMapper {
    CommitDTO toDTO(Commit commit);

    Commit toEntity(CommitDTO dto);
}
