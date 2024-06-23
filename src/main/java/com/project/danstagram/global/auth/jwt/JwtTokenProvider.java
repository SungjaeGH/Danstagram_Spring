package com.project.danstagram.global.auth.jwt;

import com.project.danstagram.domain.auth.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long REFRESH_PERIOD = 1000L * 60L * 60L * 24L * 14; // 2주
    private static final long TOKEN_PERIOD = 1000L * 60L * 30L; // 30분

    private final SecretKey secretKey;
    private final RefreshTokenService tokenService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenService tokenService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.tokenService = tokenService;
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(String memberId, String role) {

        String refreshToken = generateRefreshToken(memberId, role);
        String accessToken = generateAccessToken(memberId, role);

        // Redis 서버에 토큰 저장
        tokenService.saveTokenInfo(memberId, refreshToken, accessToken);
        return new JwtToken("Bearer", accessToken, refreshToken);
    }

    public String generateRefreshToken(String userInfo, String role) {

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
                .setExpiration(new Date(now.getTime() + REFRESH_PERIOD))
                // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(String userInfo, String role) {

        Claims claims = Jwts.claims().setSubject(userInfo);
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                        // Payload를 구성하는 속성들을 정의한다.
                        .setClaims(claims)
                        // 발행일자를 넣는다.
                        .setIssuedAt(now)
                        // 토큰의 만료일시를 설정한다.
                        .setExpiration(new Date(now.getTime() + TOKEN_PERIOD))
                        // 지정된 서명 알고리즘과 비밀 키를 사용하여 토큰을 서명한다.
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact();
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 비밀키를 설정하여 파싱한다.
                    .build()
                    .parseClaimsJws(token);  // 주어진 토큰을 파싱하여 Claims 객체를 얻는다.
            // 토큰의 만료 시간과 현재 시간비교
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());  // 만료 시간이 현재 시간 이후인지 확인하여 유효성 검사 결과를 반환

        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            System.out.println("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }

        return false;
    }

    // 토큰에서 UserInfo 정보를 추출한다.
    public String getUid(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰에서 ROLE(권한)만 추출한다.
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
