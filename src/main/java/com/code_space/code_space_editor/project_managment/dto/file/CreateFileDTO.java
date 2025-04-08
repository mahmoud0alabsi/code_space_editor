package com.code_space.code_space_editor.project_managment.dto.file;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFileDTO {
    @NotNull(message = "File name cannot be null")
    @Size(min = 1, max = 255, message = "File name must be between 1 and 255 characters")
    private String name;

    @NotNull(message = "File language cannot be null")
    @Size(min = 1, max = 50, message = "File language must be between 1 and 50 characters")
    private String language;

    @NotNull(message = "File extension cannot be null")
    @Size(min = 1, max = 10, message = "File extension must be between 1 and 10 characters")
    private String extension;

    @NotNull(message = "File content cannot be null")
    private String content;
}
