package com.code_space.code_space_editor.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    @JsonProperty("refresh_token")
    private String refreshToken;
}
