package com.code_space.code_space_editor.collaborative_coding.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.code_space.code_space_editor.collaborative_coding.dto.BranchSyncDTO;
import com.code_space.code_space_editor.collaborative_coding.dto.FileSyncDTO;
import com.code_space.code_space_editor.collaborative_coding.service.SessionStateService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SessionSyncController {
    private final SessionStateService sessionStateService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sync/project/{projectId}/branch")
    @SendTo("/topic/sync/project/{projectId}/branch")
    public BranchSyncDTO createBranch(@DestinationVariable String projectId, BranchSyncDTO branch) {
        return branch;
    }

    @MessageMapping("/sync/project/{projectId}/file")
    @SendTo("/topic/sync/project/{projectId}/file")
    public FileSyncDTO createFile(@DestinationVariable String projectId, FileSyncDTO file) {
        sessionStateService.addFile(projectId, file);
        return file;
    }

    @MessageMapping("/sync/project/{projectId}/init")
    public void initSync(@DestinationVariable String projectId, @Payload String username) {
        List<FileSyncDTO> files = sessionStateService.getFiles(projectId);
        messagingTemplate.convertAndSendToUser(username, "/queue/sync/file", files);
    }
}
