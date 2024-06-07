package com.project.danstagram.domain.auth.handler;

import com.project.danstagram.domain.auth.entity.PrincipalDetails;
import com.project.danstagram.global.auth.jwt.JwtToken;
import com.project.danstagram.global.auth.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class NormalAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        String username = principal.getUsername();
        String role = principal.getMemberRole();

        JwtToken token = jwtTokenProvider.generateToken(username, role);
        log.info("user = {}, jwtToken = {}", username, token.getAccessToken());

        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Authorization", "Bearer " + token.getAccessToken());
    }
}
