package com.project.danstagram.global.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // application.property에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(String userInfo, String role) {
        String refreshToken = generateRefreshToken(userInfo, role);
        String accessToken = generateAccessToken(userInfo, role);

        // 토큰 저장

        return new JwtToken("Bearer", accessToken, refreshToken);
    }

    public String generateRefreshToken(String userInfo, String role) {
        long refreshPeriod = 1000L * 60L * 60L * 24L * 14; // 2주

        // 새로운 클레임 객체를 생성
        Claims claims = Jwts.claims().setSubject(userInfo);
        claims.put("role", role);

        // 현재 시간과 날짜 세팅.
        Date now = new Date();

        return Jwts.builder()
                // Payload를 구성하는 속성들을 정의한다.
                .setClaims(claims)
                // 발행일자를 넣는다.
                .setIssuedAt(now)
                // 토큰의 만료일시를 설정한다.
                .setExpiration(new Date(now.getTime() + refreshPeriod))
                // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String userInfo, String role) {
        long tokenPeriod = 1000L * 60L * 30L; // 30분
        Claims claims = Jwts.claims().setSubject(userInfo);
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                        // Payload를 구성하는 속성들을 정의한다.
                        .setClaims(claims)
                        // 발행일자를 넣는다.
                        .setIssuedAt(now)
                        // 토큰의 만료일시를 설정한다.
                        .setExpiration(new Date(now.getTime() + tokenPeriod))
                        // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key) // 비밀키를 설정하여 파싱한다.
                    .build()
                    .parseClaimsJws(token);  // 주어진 토큰을 파싱하여 Claims 객체를 얻는다.
            // 토큰의 만료 시간과 현재 시간비교
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());  // 만료 시간이 현재 시간 이후인지 확인하여 유효성 검사 결과를 반환

        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 UserInfo 정보를 추출한다.
    public String getUid(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰에서 ROLE(권한)만 추출한다.
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
