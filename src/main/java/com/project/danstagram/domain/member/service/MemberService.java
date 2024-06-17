package com.project.danstagram.domain.member.service;

import com.project.danstagram.domain.member.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService {
    @Transactional
    MemberResponseDto signUp(SignUpDto signUpDto);

    @Transactional
    MemberResponseDto findMember(String memberInfo);

    @Transactional
    MemberResponseDto resetMemberPw(String memberId, ResetPwDto resetPwDto);

    @Transactional
    ProfileResponseDto updateProfile(String memberId, UpdateProfileDto updateProfileDto);

    @Transactional
    ProfileResponseDto updateProfileImg(String memberId, MultipartFile imgFile) throws IOException;

    @Transactional
    ProfileResponseDto displayProfile(String memberId);
}
