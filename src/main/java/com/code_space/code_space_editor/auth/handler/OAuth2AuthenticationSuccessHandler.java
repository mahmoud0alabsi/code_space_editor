package com.code_space.code_space_editor.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.code_space.code_space_editor.auth.entity.Role;
import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.repository.UserRepository;
import com.code_space.code_space_editor.auth.service.JwtService;
import com.code_space.code_space_editor.auth.service.RefreshTokenService;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final RefreshTokenService tokenService;

        @Value("${FRONT_END_URL}")
        private String frontEndUrl;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String registrationId = oauthToken.getAuthorizedClientRegistrationId();

                DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

                String email = oAuth2User.getAttribute("email");
                String username = oAuth2User.getAttribute("name");

                if ("github".equals(registrationId)) {
                        username = oAuth2User.getAttribute("login");
                        if (email == null) {
                                email = username + "@github.com";
                        }
                }

                if (email == null || username == null) {
                        throw new RuntimeException("Unable to extract user info from OAuth2 provider.");
                }

                final String finalEmail = email;
                final String finalUsername = username;

                // Find or create user
                User user = userRepository.findByUsername(finalUsername).orElseGet(() -> {
                        User newUser = User.builder()
                                        .email(finalEmail)
                                        .username(finalUsername)
                                        .password("")
                                        .provider(registrationId)
                                        .role(Role.USER)
                                        .build();
                        return userRepository.save(newUser);
                });

                tokenService.revokeAllRefreshTokens(user);

                String accessToken = jwtService.generateAccessToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);

                tokenService.saveRefreshToken(user, refreshToken);

                // Redirect to a frontend URL with tokens as query parameters
                String redirectUrl = UriComponentsBuilder.fromUriString(frontEndUrl + "/oauth-success")
                                .queryParam("username", user.getUsername())
                                .queryParam("accessToken", accessToken)
                                .queryParam("refreshToken", refreshToken)
                                .build().toUriString();

                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
}