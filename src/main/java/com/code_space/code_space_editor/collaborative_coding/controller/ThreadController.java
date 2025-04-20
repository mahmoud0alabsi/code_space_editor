package com.code_space.code_space_editor.collaborative_coding.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.code_space.code_space_editor.collaborative_coding.dto.ChatMessage;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ThreadController {

    @MessageMapping("/thread.sendMessage/{projectId}/{branchId}/{fileId}")
    @SendTo("/topic/thread/{projectId}/{branchId}/{fileId}")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}
