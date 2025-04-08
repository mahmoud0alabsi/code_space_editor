package com.code_space.code_space_editor.project_managment.entity.nosql;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "file_versions")
public class FileVersion {
    @Id
    private String id;

    private String fileId;
    private String content;

    private String authorId;
    private String parentVersionId;

    private Integer versionNumber;
    private String commitMessage;
    private Instant timestamp;
}
