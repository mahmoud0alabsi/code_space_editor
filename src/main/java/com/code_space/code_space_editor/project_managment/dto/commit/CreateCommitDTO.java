package com.code_space.code_space_editor.project_managment.dto.commit;

import java.util.List;

import com.code_space.code_space_editor.project_managment.dto.file.CreateFileDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommitDTO {
    @NotNull(message = "Commit message cannot be null")
    private String message;

    @NotNull(message = "Files cannot be null")
    private List<CreateFileDTO> files;
}
