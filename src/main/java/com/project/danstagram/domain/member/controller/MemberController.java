package com.project.danstagram.domain.member.controller;

import com.project.danstagram.domain.member.dto.*;
import com.project.danstagram.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<MemberResponseDto> register(@RequestBody @Valid SignUpDto signUpDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(memberService.signUp(signUpDto));
    }

    @GetMapping("/find")
    public ResponseEntity<MemberResponseDto> findMember(@RequestParam String memberInfo) {
        MemberResponseDto memberResponseDto = memberService.findMember(memberInfo);
        if (memberResponseDto.getMemberName() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(memberResponseDto);
    }

    @PatchMapping("/{memberId}/password/reset")
    public ResponseEntity<MemberResponseDto> resetPw(@PathVariable String memberId,
                                                     @RequestBody @Valid ResetPwDto resetPwDto,
                                                     Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(memberService.resetMemberPw(memberId, resetPwDto));
    }

    @PatchMapping("/{memberId}/profile/update")
    public ResponseEntity<ProfileResponseDto> updateProfile(@PathVariable String memberId, @RequestBody UpdateProfileDto updateProfileDto) {

        ProfileResponseDto profileResponseDto = memberService.updateProfile(memberId, updateProfileDto);

        if (profileResponseDto.getMemberId() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profileResponseDto);
    }

    @PatchMapping("/{memberId}/profile/image/update")
    public ResponseEntity<ProfileResponseDto> updateProfileImage(@PathVariable String memberId,
                                                                @RequestPart("image") MultipartFile imageFile) throws IOException {

        ProfileResponseDto profileResponseDto = memberService.updateProfileImg(memberId, imageFile);

        if (profileResponseDto.getMemberId() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profileResponseDto);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ProfileResponseDto> displayProfile(@PathVariable String memberId) {
        ProfileResponseDto profileResponseDto = memberService.displayProfile(memberId);

        return ResponseEntity.ok(profileResponseDto);
    }
}
