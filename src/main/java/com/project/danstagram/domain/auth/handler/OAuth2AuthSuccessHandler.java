package com.project.danstagram.domain.auth.handler;

import com.project.danstagram.domain.auth.entity.PrincipalDetails;
import com.project.danstagram.domain.member.entity.SocialMember;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.global.auth.jwt.JwtToken;
import com.project.danstagram.global.auth.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        SocialMember socialMember = principalDetails.getSocialMember();
        boolean isExist = (boolean) principalDetails.getAttributes().get("exist");

        // 회원이 존재할 경우
        if (isExist) {
            Member member = socialMember.getMember();

            String username = member.getMemberId();
            String role = principalDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .toString();

            JwtToken token = jwtTokenProvider.generateToken(username, role);
            log.info("user = {}, jwtToken = {}", username, token.getAccessToken());

            // accessToken을 쿼리스트링에 담는 url을 만들어준다.
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000")
                    .queryParam("accessToken", token.getAccessToken())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            log.info("redirect 준비");
            // 로그인 확인 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } else {
            // 회원이 존재하지 않을 경우, SocialMember의 idx를 쿼리스트링으로 전달하는 url을 만들어준다.
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000")
                    .queryParam("socialMemberIdx", socialMember.getSocialIdx())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            // 회원가입 페이지로 리다이렉트 시킨다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
