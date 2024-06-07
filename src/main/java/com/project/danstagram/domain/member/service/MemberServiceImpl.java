package com.project.danstagram.domain.member.service;

import com.project.danstagram.domain.member.dto.MemberResponseDto;
import com.project.danstagram.domain.member.dto.ResetPwDto;
import com.project.danstagram.domain.member.dto.SignUpDto;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.entity.SocialMember;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.member.repository.SocialMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;
    private final PasswordEncoder passwordEncoder;

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

        Member savedMember = signUpDto.toEntity(encodePw);

        // 소셜 회원일 경우, 1:N 연결
        if (signUpDto.getSocialMemberIdx() != 0) {
            SocialMember socialMember = socialMemberRepository
                    .findBySocialIdx(signUpDto.getSocialMemberIdx())
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 소셜 회원을 찾을 수 없습니다."));

            savedMember.putSocialMember(socialMember);
        }

        return MemberResponseDto.toResponseDto(memberRepository.save(savedMember));
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

    @Transactional
    @Override
    public MemberResponseDto findMember(String memberInfo) {
        Member member = memberRepository.findByMemberIdOrMemberPhoneOrMemberEmail(memberInfo, memberInfo, memberInfo)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        return MemberResponseDto.toResponseDto(member);
    }

    @Transactional
    @Override
    public MemberResponseDto resetMemberPw(String memberId, ResetPwDto resetPwDto) {
        if (!resetPwDto.getNewMemberPw().equals(resetPwDto.getConfirmMemberPw())) {
            throw new IllegalArgumentException("입력한 비밀번호가 일치하지 않습니다.");
        }

        Member changedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        String encodedNewPw = passwordEncoder.encode(resetPwDto.getNewMemberPw());
        changedMember.changePw(encodedNewPw);

        return MemberResponseDto.toResponseDto(memberRepository.save(changedMember));
    }
}