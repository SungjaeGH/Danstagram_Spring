package com.project.danstagram.domain.auth.service;

import com.project.danstagram.global.auth.jwt.JwtToken;
import com.project.danstagram.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken normalLogin(String memberInfo, String memberPw) {
        // 1. memberInfo + memberPw 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberInfo, memberPw);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. UserDetails 내 User와 Role 정보를 추출
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        String userInfo = userDetail.getUsername();
        String role = userDetail.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("회원의 권한 정보가 누락되었음."))
                .toString();

        // 4. 추출한 인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(userInfo, role);
    }
}
