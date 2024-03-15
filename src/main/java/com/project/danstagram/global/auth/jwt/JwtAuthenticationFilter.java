package com.project.danstagram.global.auth.jwt;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request Header에서 AccessToken을 가져온다.
        String accessToken = request.getHeader("Authorization");

        // 토큰 검사 생략 (모두 허용 URL의 경우 토큰 검사 통과)
        if (!StringUtils.hasText(accessToken)) {
            doFilter(request, response, filterChain);
            return;
        }

        // AccessToken을 검증
        if (!jwtTokenProvider.verifyToken(accessToken)) {
            throw new JwtException("Access Token 만료!");
        }

        // AccessToken 내부의 payload에 있는 email로 user를 조회한다. 없다면 예외를 발생시킨다 -> 정상 케이스가 아님
        String userInfo = jwtTokenProvider.getUid(accessToken);
        Member member = memberRepository.findByMemberIdOrMemberPhoneOrMemberEmail(userInfo, userInfo, userInfo)
                .orElseThrow(IllegalStateException::new);

        // SecurityContext에 인증 객체를 등록해준다.
        Authentication auth = getAuthentication(member);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthentication(Member member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getMemberRole().toString())));
    }
}
