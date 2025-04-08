package com.code_space.code_space_editor.project_managment.dto.branch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBranchDTO {
    @NotNull(message = "Branch name cannot be null")
    @Size(min = 1, max = 255, message = "Branch name must be between 1 and 255 characters")
    private String name;
}
