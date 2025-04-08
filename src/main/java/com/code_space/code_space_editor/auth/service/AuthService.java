package com.code_space.code_space_editor.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.code_space.code_space_editor.auth.dto.*;
import com.code_space.code_space_editor.auth.entity.Role;
import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final RefreshTokenService tokenService;
        private final AuthenticationManager authenticationManager;

        public void register(RegisterRequest request) {
                var user = User.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .provider("local")
                                .role(Role.USER)
                                .build();
                userRepository.save(user);
        }

        public AuthResponse login(LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(),
                                                        request.getPassword()));

                        var user = userRepository.findByUsername(request.getUsername())
                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                        "Invalid username or password"));

                        String accessToken = jwtService.generateAccessToken(user);
                        String refreshToken = jwtService.generateRefreshToken(user);

                        tokenService.revokeAllRefreshTokens(user);
                        tokenService.saveRefreshToken(user, refreshToken);

                        return new AuthResponse(accessToken, refreshToken);
                } catch (Exception e) {
                        throw new UsernameNotFoundException("Invalid username or password", e);
                }
        }

        public AuthResponse refreshToken(RefreshTokenRequest request) {
                String refreshToken = request.getRefreshToken();
                String username = jwtService.extractUsername(refreshToken);

                var user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                var storedToken = tokenService.getValidRefreshToken(refreshToken)
                                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

                // Invalidate old token (rotation)
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenService.save(storedToken);

                // Generate new tokens
                String newAccessToken = jwtService.generateAccessToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);

                tokenService.saveRefreshToken(user, newRefreshToken);

                return new AuthResponse(newAccessToken, newRefreshToken);
        }
}
