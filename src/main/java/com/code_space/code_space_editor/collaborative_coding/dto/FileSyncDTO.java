package com.code_space.code_space_editor.collaborative_coding.dto;

import lombok.Data;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileSyncDTO {
    private String id;
    private String projectId;
    private String branchId;

    private String name;
    private String content;
    private String extension;
    private String language;
    private Instant createdAt;

    private String author;
}