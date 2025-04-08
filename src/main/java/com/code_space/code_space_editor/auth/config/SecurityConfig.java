package com.code_space.code_space_editor.auth.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.code_space.code_space_editor.auth.handler.CustomLogoutSuccessHandler;
import com.code_space.code_space_editor.auth.handler.OAuth2AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private static final String[] WHITE_LIST_URLS = {
                        "/api/v1/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/hello/**",
        };

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;
        private final LogoutHandler logoutHandler;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final CustomLogoutSuccessHandler logoutSuccessHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(csrf -> csrf.disable())
                        .authorizeHttpRequests(auth -> auth
                                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                                        .anyRequest().authenticated())
                        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                        .formLogin(AbstractHttpConfigurer::disable)
                        .exceptionHandling(exception -> exception
                                        .authenticationEntryPoint((request, response, authException) -> {
                                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                response.setContentType("application/json");
                                                response.getWriter().write("{\"error\": \"Unauthorized request\"}");
                                        }))
                        .oauth2Login(oauth2 -> oauth2
                                        .successHandler(oAuth2AuthenticationSuccessHandler))
                        .logout(logout -> logout
                                        .logoutUrl("/api/v1/auth/logout")
                                        .addLogoutHandler(logoutHandler)
                                        .logoutSuccessHandler(logoutSuccessHandler));

                return http.build();
        }
}