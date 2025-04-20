package com.code_space.code_space_editor.collaborative_coding.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.code_space.code_space_editor.collaborative_coding.dto.ChatMessage;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    @MessageMapping("/chat.sendMessage/{projectId}")
    @SendTo("/topic/chat/{projectId}")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}
