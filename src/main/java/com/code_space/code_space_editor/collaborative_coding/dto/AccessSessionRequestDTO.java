package com.code_space.code_space_editor.collaborative_coding.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessSessionRequestDTO {
    @NotNull(message = "Project ID must not be null")
    @JsonProperty("projectId")
    private Long projectId;

    @NotNull(message = "Branch ID must not be null")
    @JsonProperty("branchId")
    private Long branchId;

    @NotNull(message = "File ID must not be null")
    @JsonProperty("fileId")
    private Long fileId;
}
