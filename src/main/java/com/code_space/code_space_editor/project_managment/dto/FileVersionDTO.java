package com.code_space.code_space_editor.project_managment.dto;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVersionDTO {
    private String id;
    private String fileId;
    private String content;
    private String authorId;
    private String parentVersionId;
    private Integer versionNumber;
    private String commitMessage;
    private Instant timestamp;
}

