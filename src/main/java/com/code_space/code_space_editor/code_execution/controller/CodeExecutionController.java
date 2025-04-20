package com.code_space.code_space_editor.code_execution.controller;

import com.code_space.code_space_editor.code_execution.dto.CodeExecutionRequest;
import com.code_space.code_space_editor.code_execution.dto.CodeExecutionResult;
import com.code_space.code_space_editor.code_execution.service.CodeExecutionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.code_space.code_space_editor.auth.utility.AuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/execute")
@RequiredArgsConstructor
public class CodeExecutionController {

    private final CodeExecutionService codeExecutionService;
    private final AuthUtils authUtils;

    @PostMapping
    public ResponseEntity<CodeExecutionResult> executeCode(
            @RequestBody CodeExecutionRequest request) {

        Long userId = authUtils.getCurrentUserId();

        CodeExecutionResult result = codeExecutionService.executeCode(
                request.getCode(),
                request.getLanguage(),
                request.getArgs(),
                request.getTimeoutSeconds());

        result.setExecutedBy(userId);
        return ResponseEntity.ok(result);
    }

}
