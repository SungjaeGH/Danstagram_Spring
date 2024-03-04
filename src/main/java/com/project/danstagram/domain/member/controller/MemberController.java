package com.project.danstagram.domain.member.controller;

import com.project.danstagram.domain.member.dto.MemberResponseDto;
import com.project.danstagram.domain.member.dto.SignInDto;
import com.project.danstagram.domain.member.dto.SignUpDto;
import com.project.danstagram.domain.member.service.MemberService;
import com.project.danstagram.global.auth.jwt.JwtToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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

    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponseDto> signUp(@RequestBody @Valid SignUpDto signUpDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        MemberResponseDto savedMemberResponseDto = memberService.signUp(signUpDto);
        return ResponseEntity.ok(savedMemberResponseDto);
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
