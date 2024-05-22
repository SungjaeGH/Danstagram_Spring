package com.project.danstagram.domain.auth.handler;

import com.project.danstagram.global.auth.jwt.JwtToken;
import com.project.danstagram.global.auth.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class NormalAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        String username = principal.getUsername();
        String role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .toString();

        JwtToken token = jwtTokenProvider.generateToken(username, role);
        log.info("user = {}, jwtToken = {}", username, token.getAccessToken());

        // 쿼리 파라미터에 토큰 추가
        String targetUrl = "http://localhost:3000";
        String redirectUrl = String.format("%s?accessToken=%s",
                targetUrl,
                URLEncoder.encode(token.getAccessToken(), StandardCharsets.UTF_8));

        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(redirectUrl);
    }
}
