package com.project.danstagram.domain.auth.service;

import com.project.danstagram.domain.auth.entity.PrincipalDetails;
import com.project.danstagram.domain.member.entity.SocialMember;
import com.project.danstagram.domain.member.repository.SocialMemberRepository;
import com.project.danstagram.global.auth.oauth2.FacebookUserInfo;
import com.project.danstagram.global.auth.oauth2.KakaoUserInfo;
import com.project.danstagram.global.auth.oauth2.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalOAuthMemberService extends DefaultOAuth2UserService {
    private final SocialMemberRepository socialMemberRepository;

    /*
        * OAuth 2.0 인증을 통해 SocialMember 정보를 가져오는 역할.
            * 정보가 존재하지 않을 경우, create 수행
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;
        if (provider.equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());

        } else if (provider.equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        } else {
            throw new UnsupportedOperationException("Unsupported OAuth2 provider : " + provider);
        }

        String socialEmail = oAuth2UserInfo.getEmail();

        Optional<SocialMember> socialMemberOptional = socialMemberRepository.findBySocialEmail(socialEmail);
        boolean isExist = socialMemberOptional.isPresent();

        // Social Member 정보 존재 유무에 따른 Member 생성 여부 추가
        Map<String, Object> newAttributes = new HashMap<>(oAuth2User.getAttributes());
        newAttributes.put("exist", isExist);

        // Social Member 정보 없을 경우, DB 저장
        if (!isExist) {
            SocialMember saveSocialMember = socialMemberRepository.save(createSocialMember(oAuth2UserInfo));
            return new PrincipalDetails(saveSocialMember, newAttributes);
        }

        return new PrincipalDetails(socialMemberOptional.get(), newAttributes);
    }

    private SocialMember createSocialMember(OAuth2UserInfo oAuth2UserInfo) {
        return SocialMember.builder()
                .socialEmail(oAuth2UserInfo.getEmail())
                .providerId(oAuth2UserInfo.getProviderId())
                .provider(oAuth2UserInfo.getProvider())
                .socialName(oAuth2UserInfo.getName())
                .build();
    }
}
