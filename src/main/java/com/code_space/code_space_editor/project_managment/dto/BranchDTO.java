package com.code_space.code_space_editor.project_managment.dto;

import java.time.Instant;
import java.util.List;

import com.code_space.code_space_editor.project_managment.entity.sql.Project;
import com.code_space.code_space_editor.project_managment.entity.sql.File;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {
    private Long id;
    private Project project;
    private Long createdBy;

    private String name;
    private Long baseBranchId;

    private Instant createdAt;
    private Instant updatedAt;

    private List<File> files;
}
