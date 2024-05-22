package com.project.danstagram.global.config;

import com.project.danstagram.domain.auth.handler.AuthFailureHandler;
import com.project.danstagram.domain.auth.handler.NormalAuthSuccessHandler;
import com.project.danstagram.domain.auth.handler.OAuth2AuthSuccessHandler;
import com.project.danstagram.domain.auth.service.PrincipalOAuthMemberService;
import com.project.danstagram.global.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PrincipalOAuthMemberService principalOAuthMemberService;
    private final NormalAuthSuccessHandler normalAuthSuccessHandler;
    private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // CORS 설정
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 해당 API에 대해서는 모든 요청을 허가
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/member/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        // USER 권한이 있어야 요청할 수 있음
                        .requestMatchers("/api/member/req").hasRole("USER")
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        // 사용자 정의 로그인 페이지
                        .loginPage("/login")
                        // Security가 로그인 과정을 수행하기 위한 요청 URI
                        .loginProcessingUrl("/api/auth/login")
                        // login form의 default 파라미터 설정
                        .usernameParameter("memberInfo")
                        .passwordParameter("memberPw")
                        // Handler
                        .successHandler(normalAuthSuccessHandler)
                        .failureHandler(authFailureHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/login/oauth2/callback/*")
                        )
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(principalOAuthMemberService)
                        )
                        .successHandler(oAuth2AuthSuccessHandler)
                        .failureHandler(authFailureHandler)
                )

                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * CORS 설정 Config
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /**
     * Security Debug 설정 Config
     */

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.debug(securityDebug));
    }
}
