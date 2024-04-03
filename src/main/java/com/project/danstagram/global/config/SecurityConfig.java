package com.project.danstagram.global.config;

import com.project.danstagram.domain.auth.handler.OAuth2AuthenticationFailureHandler;
import com.project.danstagram.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.project.danstagram.domain.auth.service.PrincipalOAuthMemberService;
import com.project.danstagram.global.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PrincipalOAuthMemberService principalOAuthMemberService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 해당 API에 대해서는 모든 요청을 허가
                        .requestMatchers("/members/sign-up").permitAll()
                        .requestMatchers("/members/sign-in").permitAll()
                        .requestMatchers("/members/password/reset/{memberIdx}").permitAll()
                        // USER 권한이 있어야 요청할 수 있음
                        .requestMatchers("/members/test").hasRole("USER")
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login
                        .loginPage("/sign-in-form")         // 사용자 정의 로그인 페이지
                        .loginProcessingUrl("/sign-in")     // Security가 로그인 과정을 수행하기 위한 요청 URI
                        .defaultSuccessUrl("/")             // 로그인 성공 후 이동 페이지
                        // login form의 default 파라미터 설정
                        .usernameParameter("memberInfo")
                        .passwordParameter("memberPw")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/sign-in-form")
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(principalOAuthMemberService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
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
}
