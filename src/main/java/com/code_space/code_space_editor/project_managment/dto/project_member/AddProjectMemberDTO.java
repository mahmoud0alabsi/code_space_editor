package com.code_space.code_space_editor.project_managment.dto.project_member;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddProjectMemberDTO {
    @NotNull(message = "Username must not be null")
    private String username;

    @NotNull(message = "Role must not be null")
    private ProjectRole role;
}
