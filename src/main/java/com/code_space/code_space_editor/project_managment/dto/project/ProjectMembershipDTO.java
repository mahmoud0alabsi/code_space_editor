package com.code_space.code_space_editor.project_managment.dto.project;

import java.time.Instant;

import com.code_space.code_space_editor.project_managment.entity.enums.ProjectRole;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMembershipDTO {
    private Long id;
    private String ownerName;

    private ProjectRole role;

    private String name;
    private String description;

    private Instant createdAt;
    private Instant updatedAt;
}
