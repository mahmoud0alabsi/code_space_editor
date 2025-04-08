package com.code_space.code_space_editor.project_managment.dto;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;
import com.code_space.code_space_editor.project_managment.entity.sql.Project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {
    private Long id;

    private Project project;

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Role must not be null")
    private ProjectRole role;
}
