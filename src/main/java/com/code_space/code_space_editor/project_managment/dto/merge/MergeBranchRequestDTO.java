package com.code_space.code_space_editor.project_managment.dto.merge;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergeBranchRequestDTO {
    @NotNull(message = "Target branch ID cannot be null")
    private Long targetBranchId;
    @NotNull(message = "Source branch ID cannot be null")
    private Long sourceBranchId;
}
