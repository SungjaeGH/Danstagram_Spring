package com.project.danstagram.domain.member.controller;

import com.project.danstagram.domain.member.dto.SignInDto;
import com.project.danstagram.domain.member.service.MemberService;
import com.project.danstagram.global.auth.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String memberInfo = signInDto.getMemberInfo();
        String memberPw = signInDto.getMemberPw();

        JwtToken jwtToken = memberService.signIn(memberInfo, memberPw);
        log.info("request memberInfo = {}, password = {}", memberInfo, memberPw);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
