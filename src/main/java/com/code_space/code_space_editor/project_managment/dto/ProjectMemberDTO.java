package com.code_space.code_space_editor.project_managment.dto;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {
    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Username must not be null")
    private String username;

    @NotNull(message = "Role must not be null")
    private ProjectRole role;
}
