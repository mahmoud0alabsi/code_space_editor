package com.code_space.code_space_editor.code_execution.dto;

import java.util.List;

import lombok.Data;

@Data
public class CodeExecutionRequest {
    private String code;
    private String language;
    private List<String> args;
    private String sessionId;
    private Integer timeoutSeconds = 10;
}
