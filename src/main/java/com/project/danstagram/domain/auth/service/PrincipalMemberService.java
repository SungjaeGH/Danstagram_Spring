package com.project.danstagram.domain.auth.service;

import com.project.danstagram.domain.auth.entity.PrincipalDetails;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.entity.Role;
import com.project.danstagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalMemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userInfo) {
        return memberRepository.findByMemberIdOrMemberPhoneOrMemberEmail(userInfo, userInfo, userInfo)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        return User.builder()
                .username(principalDetails.getUsername())
                .password(principalDetails.getPassword())
                .roles(String.valueOf(Role.USER))
                .build();
    }
}