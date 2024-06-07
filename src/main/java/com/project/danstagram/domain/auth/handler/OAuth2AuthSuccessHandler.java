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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            String role = principalDetails.getMemberRole();

            JwtToken token = jwtTokenProvider.generateToken(username, role);
            log.info("user = {}, jwtToken = {}", username, token.getAccessToken());

            // accessToken을 쿼리스트링에 담는 url을 만들어준다.
            response.setStatus(HttpStatus.OK.value());
            response.setHeader("Authorization", "Bearer " + token.getAccessToken());

        } else {
            // 회원이 존재하지 않을 경우, header에 SocialMember의 email 정보를 포함시켜 전달한다.
            response.setStatus(HttpStatus.OK.value());
            response.setHeader("SocialEmail", socialMember.getSocialEmail());
        }
    }
}
