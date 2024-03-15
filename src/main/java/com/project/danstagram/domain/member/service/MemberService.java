package com.project.danstagram.domain.member.service;

import com.project.danstagram.domain.member.dto.MemberResponseDto;
import com.project.danstagram.domain.member.dto.ResetPwDto;
import com.project.danstagram.domain.member.dto.SignUpDto;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {
    @Transactional
    MemberResponseDto signUp(SignUpDto signUpDto);

    @Transactional
    MemberResponseDto findMember(String memberInfo);

    @Transactional
    MemberResponseDto resetMemberPw(Long memberIdx, ResetPwDto resetPwDto);
}
