package com.project.danstagram.domain.member.service;

import com.project.danstagram.global.auth.jwt.JwtToken;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {
    @Transactional
    JwtToken signIn(String memberInfo, String memberPw);
}
