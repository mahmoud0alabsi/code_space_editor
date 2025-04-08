package com.code_space.code_space_editor.project_managment.dto.file;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileVersionDTO {
    @NotNull(message = "File content cannot be null")
    private String content;

    @NotNull(message = "Message cannot be null")
    private String message;

}
