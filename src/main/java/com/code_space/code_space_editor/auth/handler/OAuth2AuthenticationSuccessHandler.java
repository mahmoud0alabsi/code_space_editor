package com.code_space.code_space_editor.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.code_space.code_space_editor.auth.entity.Role;
import com.code_space.code_space_editor.auth.entity.User;
import com.code_space.code_space_editor.auth.repository.UserRepository;
import com.code_space.code_space_editor.auth.service.JwtService;
import com.code_space.code_space_editor.auth.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final JwtService jwtService;
        private final UserRepository userRepository;
        private final RefreshTokenService tokenService;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // e.g., "google" or "github"

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
                // final String finalUsername = username;

                // Find or create user
                User user = userRepository.findByUsername(email).orElseGet(() -> {
                        User newUser = User.builder()
                                        .email(finalEmail)
                                        .username(finalEmail)
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

                // Return tokens in response
                response.setContentType("application/json");
                response.getWriter().write(
                                new ObjectMapper().writeValueAsString(
                                                Map.of(
                                                                "accessToken", accessToken,
                                                                "refreshToken", refreshToken)));
                response.getWriter().flush();

                // Optionally, redirect to a frontend URL with tokens as query parameters
                // String redirectUrl =
                // UriComponentsBuilder.fromUriString("http://localhost:3000/oauth-success")
                // .queryParam("accessToken", accessToken)
                // .queryParam("refreshToken", refreshToken)
                // .build().toUriString();

                // response.sendRedirect(redirectUrl);

        }
}

// Optional: If you want to redirect to a frontend URL with tokens as query
// parameters, uncomment the above lines and create a corresponding React
// component to handle the redirect.
// import { useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';

// const OAuthSuccess = () => {
// const navigate = useNavigate();

// useEffect(() => {
// const params = new URLSearchParams(window.location.search);
// const accessToken = params.get('accessToken');
// const refreshToken = params.get('refreshToken');

// if (accessToken && refreshToken) {
// localStorage.setItem('accessToken', accessToken);
// localStorage.setItem('refreshToken', refreshToken);
// navigate('/dashboard'); // or your app's home page
// } else {
// // handle error
// navigate('/login');
// }
// }, []);

// return <div>Logging you in...</div>;
// };

// export default OAuthSuccess;
