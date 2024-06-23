package com.project.danstagram.domain.auth.service;

import com.project.danstagram.domain.auth.entity.RefreshToken;
import com.project.danstagram.domain.auth.repository.RefreshTokenRepository;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final TimeUtil timeUtil;

    @Transactional
    public void saveTokenInfo(String memberId, String refreshToken, String accessToken) {

        // 토큰 정보 생성
        refreshTokenRepository.save(new RefreshToken(memberId, accessToken, refreshToken));

        // 로그인 기록
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        member.changeLoginState(timeUtil.getCurrTime(TimeFormat.TimeFormat1));
        memberRepository.save(member);
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {

        // 토큰 정보 삭제
        RefreshToken token = refreshTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(IllegalArgumentException::new);

        refreshTokenRepository.delete(token);

        // 로그아웃 기록
        Member member = memberRepository.findByMemberId(token.getId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        member.changeLogoutState(timeUtil.getCurrTime(TimeFormat.TimeFormat1));
        memberRepository.save(member);
    }
}
