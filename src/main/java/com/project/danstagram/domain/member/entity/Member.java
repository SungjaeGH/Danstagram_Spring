package com.project.danstagram.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "memberIdx")
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "member_idx", updatable = false, unique = true, nullable = false)
    private Long memberIdx;

    @Column(name = "member_id", unique = true, nullable = false)
    private String memberId;

    @Column(name = "member_phone", unique = true, nullable = false)
    private String memberPhone;

    @Column(name = "member_email", unique = true, nullable = false)
    private String memberEmail;

    @Column(name = "member_pw", nullable = false)
    private String memberPw;

    @Column(name = "member_role")
    private String memberRole;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "member_introduce")
    private String memberIntroduce;

    @Column(name = "member_website")
    private String memberWebsite;

    @Column(name = "member_gender")
    private String memberGender;

    @Column(name = "member_image_url")
    private String memberImageUrl;

    @Column(name = "member_image_type")
    private String memberImageType;

    @Column(name = "member_image_name")
    private String memberImageName;

    @Column(name = "member_image_uuid")
    private String memberImageUuid;

    @Column(name = "member_status")
    private String memberStatus;

    @Column(name = "member_login_date")
    private String memberLoginDate;

    @Column(name = "member_logout_date")
    private String memberLogoutDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.memberPw;
    }

    @Override
    public String getUsername() {
        return this.memberId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}