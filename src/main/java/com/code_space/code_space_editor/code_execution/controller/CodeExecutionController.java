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
    // private SimpMessagingTemplate messagingTemplate;
    private final AuthUtils authUtils;

    @PostMapping
    public ResponseEntity<CodeExecutionResult> executeCode(
            @RequestBody CodeExecutionRequest request) {

        // Get current user
        Long userId = authUtils.getCurrentUserId();

        // Execute code
        CodeExecutionResult result = codeExecutionService.executeCode(
                request.getCode(),
                request.getLanguage(),
                request.getArgs(),
                request.getTimeoutSeconds());

        // Set user who executed the code
        result.setExecutedBy(userId);

        // Broadcast results to others in session
        // if (request.getSessionId() != null) {
        // messagingTemplate.convertAndSend(
        // "/topic/execution/" + request.getSessionId(),
        // result);
        // }

        return ResponseEntity.ok(result);
    }

}
