package com.code_space.code_space_editor.project_managment.dto.file;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private Long id;
    private Long author;

    private String name;
    private String extension;
    private String language;
    private Instant createdAt;

    private String content;
}
