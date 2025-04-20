package com.code_space.code_space_editor.auth.service;

import com.code_space.code_space_editor.auth.entity.RefreshToken;
import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository tokenRepository;

    // Tokens rotation
    public void revokeAllRefreshTokens(User user) {
        List<RefreshToken> validTokens = tokenRepository.findAllValidTokenByUser(user.getId().intValue());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validTokens);
        }
    }

    public void saveRefreshToken(User user, String token) {
        RefreshToken refToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(refToken);
    }

    public Optional<RefreshToken> getValidRefreshToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired() && !t.isRevoked());
    }

    public void save(RefreshToken token) {
        tokenRepository.save(token);
    }
}
