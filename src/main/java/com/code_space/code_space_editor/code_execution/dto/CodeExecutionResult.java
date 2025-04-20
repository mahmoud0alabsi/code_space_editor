package com.code_space.code_space_editor.code_execution.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CodeExecutionResult {
    private String stdout;
    private String stderr;
    private int exitCode;
    private String compilationOutput;
    private boolean success;
    private long executionTimeMs;
    private Long memoryUsageBytes;
    private String errorMessage;
    private LocalDateTime executedAt = LocalDateTime.now();
    private Long executedBy;
}