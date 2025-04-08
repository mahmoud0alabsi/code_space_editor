package com.code_space.code_space_editor.auth.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
