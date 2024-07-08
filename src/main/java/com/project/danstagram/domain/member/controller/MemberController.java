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
    public ResponseEntity<MemberResponse.SignUp> register(@RequestBody @Valid MemberRequest.SignUp signUpDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(memberService.signUp(signUpDto));
    }

    @PatchMapping("/{memberId}/password/reset")
    public ResponseEntity<MemberResponse.ResetPw> resetPw(@PathVariable String memberId,
                                                          @RequestBody @Valid MemberRequest.ResetPw resetPwDto,
                                                          Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(memberService.resetMemberPw(resetPwDto.appendDto(memberId)));
    }

    @PatchMapping("/{memberId}/profile/update")
    public ResponseEntity<MemberResponse.UpdateProfile> updateProfile(@PathVariable String memberId,
                                                                      @RequestBody MemberRequest.UpdateProfile updateProfileDto) {

        return ResponseEntity.ok(memberService.updateProfile(updateProfileDto.appendDto(memberId)));
    }

    @PatchMapping("/{memberId}/profile/image/update")
    public ResponseEntity<MemberResponse.UpdateProfileImg> updateProfileImage(@PathVariable String memberId,
                                                                              @RequestPart("image") MultipartFile imageFile) throws IOException {

        return ResponseEntity.ok(memberService.updateProfileImg(memberId, imageFile));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse.DisplayProfile> displayProfile(@PathVariable String memberId) {

        return ResponseEntity.ok(memberService.displayProfile(memberId));
    }

    @GetMapping("/{memberId}/req")
    public ResponseEntity<MemberResponse.DisplayMainList> displayMain(@PathVariable String memberId,
                                                                      @RequestParam(name = "post-scroll-size") int postScrollSize,
                                                                      @RequestParam(name = "last-postidx", required = false) Long lastPostIdx,
                                                                      @RequestParam(name = "story-scroll-size") int storyScrollSize,
                                                                      @RequestParam(name = "last-storyidx", required = false) Long lastStoryIdx) {

        MemberRequest.DisplayMain request = MemberRequest.DisplayMain.builder()
                .memberId(memberId)
                .postScrollSize(postScrollSize)
                .lastPostIdx(lastPostIdx)
                .storyScrollSize(storyScrollSize)
                .lastStoryIdx(lastStoryIdx)
                .build();

        return ResponseEntity.ok(memberService.displayMain(request));
    }

    @PatchMapping("/{memberId}/update/delete-status")
    public ResponseEntity<MemberResponse.UpdateDeleteStatus> updateDeleteStatus(@PathVariable String memberId,
                                                                                @RequestBody MemberRequest.UpdateDeleteStatus request) {

        return ResponseEntity.ok(memberService.updateDeleteStatus(request.appendDto(memberId)));
    }
}
