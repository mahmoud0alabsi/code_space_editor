package com.code_space.code_space_editor.collaborative_coding.dto;

import java.time.Instant;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchSyncDTO {
    private String id;
    private String projectId;

    private String name;
    private Instant createdAt;
    private Instant updatedAt;

    private String author;
}
