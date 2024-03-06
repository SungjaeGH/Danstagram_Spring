package com.project.danstagram.domain.member.controller;

import com.project.danstagram.domain.member.dto.MemberResponseDto;
import com.project.danstagram.domain.member.dto.ResetPwDto;
import com.project.danstagram.domain.member.dto.SignInDto;
import com.project.danstagram.domain.member.dto.SignUpDto;
import com.project.danstagram.domain.member.service.MemberService;
import com.project.danstagram.global.auth.jwt.JwtToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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

        return memberService.signIn(memberInfo, memberPw);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponseDto> signUp(@RequestBody @Valid SignUpDto signUpDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(memberService.signUp(signUpDto));
    }

    @GetMapping ("/find")
    public ResponseEntity<MemberResponseDto> findMember(@RequestParam String memberInfo) {
        MemberResponseDto memberResponseDto = memberService.findMember(memberInfo);
        if (memberResponseDto.getMemberIdx() == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(memberResponseDto);
    }

    @PatchMapping("/password/reset/{memberIdx}")
    public ResponseEntity<MemberResponseDto> resetPw(@PathVariable Long memberIdx,
                                                     @RequestBody @Valid ResetPwDto resetPwDto,
                                                     Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(memberService.resetMemberPw(memberIdx, resetPwDto));
    }


    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
