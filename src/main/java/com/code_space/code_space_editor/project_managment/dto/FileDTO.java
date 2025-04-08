package com.code_space.code_space_editor.project_managment.dto;

import java.time.Instant;

import com.code_space.code_space_editor.project_managment.entity.sql.Branch;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private Long id;
    private Branch branch;
    private Long createdBy;
    private String name;
    private String path;
    private String language;
    private Instant createdAt;
    private Instant updatedAt;
}
