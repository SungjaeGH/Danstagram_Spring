package com.project.danstagram.domain.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        StringBuilder redirectUrl = new StringBuilder();
        redirectUrl.append("http://localhost:3000/login?error=");

        // 인증 실패 시, 메인 페이지로 이동
        try {
            if (exception instanceof BadCredentialsException) {
                // 유효하지 않는 Username or Password
                redirectUrl.append("credentials");

            } else if (exception instanceof DisabledException) {
                // 인증 거부
                redirectUrl.append("locked");

            } else if (exception instanceof CredentialsExpiredException) {
                // 비밀번호 만료
                redirectUrl.append("expired");

            } else {
                redirectUrl.append("true");
            }

            // 리다이렉트 실행
            response.sendRedirect(String.valueOf(redirectUrl));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
