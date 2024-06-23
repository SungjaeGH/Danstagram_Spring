package com.project.danstagram.domain.auth.controller;

import com.project.danstagram.domain.auth.entity.RefreshToken;
import com.project.danstagram.domain.auth.repository.RefreshTokenRepository;
import com.project.danstagram.domain.auth.service.RefreshTokenService;
import com.project.danstagram.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") final String accessToken) {

        // 엑세스 토큰으로 현재 Redis 정보 삭제
        refreshTokenService.removeRefreshToken(accessToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") final String accessToken) {

        // 액세스 토큰으로 Refresh 토큰 객체를 조회
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccessToken(accessToken);

        // RefreshToken이 존재하고 유효하다면 실행
        if (refreshToken.isPresent() && jwtTokenProvider.verifyToken(refreshToken.get().getRefreshToken())) {
            // RefreshToken 객체를 꺼내온다.
            RefreshToken resultToken = refreshToken.get();
            // 권한과 아이디를 추출해 새로운 액세스토큰을 만든다.
            String newAccessToken = jwtTokenProvider.generateAccessToken(resultToken.getId(), jwtTokenProvider.getRole(resultToken.getRefreshToken()));
            // 액세스 토큰의 값을 수정해준다.
            resultToken.updateAccessToken(newAccessToken);
            refreshTokenRepository.save(resultToken);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + newAccessToken)
                    .build();
        }

        return ResponseEntity.badRequest().build();
    }
}
