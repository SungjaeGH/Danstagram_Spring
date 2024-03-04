package com.project.danstagram.domain.member.service;

import com.project.danstagram.domain.member.dto.MemberResponseDto;
import com.project.danstagram.domain.member.dto.SignUpDto;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.global.auth.jwt.JwtToken;
import com.project.danstagram.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public JwtToken signIn(String memberInfo, String memberPw) {
        // 1. memberInfo + memberPw 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberInfo, memberPw);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional
    @Override
    public MemberResponseDto signUp(SignUpDto signUpDto) {
        if (memberRepository.existsByMemberId(signUpDto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용중인 사용자 이름입니다.");
        }

        // 정규식 체크
        int checkCode = checkMemberInfoValidation(signUpDto.getMemberInfo());
        switch (checkCode) {
            case 1:
                signUpDto.setMemberEmail(signUpDto.getMemberInfo());
                break;

            case 2:
                signUpDto.setMemberPhone(signUpDto.getMemberInfo());
                break;

            default:
                throw new IllegalArgumentException("올바른 이메일 또는 휴대전화 형식이 아닙니다.");
        }

        String encodePw = passwordEncoder.encode(signUpDto.getMemberPw());
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        return MemberResponseDto.toResponseDto(memberRepository.save(signUpDto.toEntity(encodePw, roles)));
    }

    private static int checkMemberInfoValidation(String memberInfo) {
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        String phonePattern = "^(\\d{3})-(\\d{3,4})-(\\d{4})$";
        int resultCode = 0;

        if (Pattern.matches(emailPattern, memberInfo)) {
            resultCode = 1;

        } else if (Pattern.matches(phonePattern, memberInfo)) {
            resultCode = 2;
        }

        return resultCode;
    }
}
