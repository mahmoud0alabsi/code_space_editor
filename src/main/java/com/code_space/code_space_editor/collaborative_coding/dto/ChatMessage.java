package com.code_space.code_space_editor.collaborative_coding.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String content;
    private String sender;
    private String timestamp;
}
