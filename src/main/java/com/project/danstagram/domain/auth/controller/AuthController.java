package com.project.danstagram.domain.auth.controller;

import com.project.danstagram.domain.auth.dto.SignInDto;
import com.project.danstagram.domain.auth.service.AuthService;
import com.project.danstagram.global.auth.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JwtToken login(@RequestBody SignInDto signInDto) {
        String memberInfo = signInDto.getMemberInfo();
        String memberPw = signInDto.getMemberPw();

        return authService.normalLogin(memberInfo, memberPw);
    }
}
