package com.project.danstagram.domain.auth.entity;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.entity.Role;
import com.project.danstagram.domain.member.entity.SocialMember;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
    private Member member;
    private SocialMember socialMember;
    private Map<String, Object> attributes;

    // normal login
    public PrincipalDetails(Member member) {
        this.member = member;
    }

    // social login
    public PrincipalDetails(SocialMember socialMember, Map<String, Object> attributes) {
        this.socialMember = socialMember;
        this.attributes = attributes;
    }

    /*
     * OAuth2User Interface Method
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /*
     * UserDetails Interface Method
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        if (member != null) {
            collection.add(() -> String.valueOf(member.getMemberRole()));

        } else if (socialMember != null) {
            collection.add(() -> String.valueOf(Role.USER));
        }

        return collection;
    }

    @Override
    public String getPassword() {
        return (member != null) ? member.getMemberPw() : null;
    }

    @Override
    public String getUsername() {
        if (this.member != null) {
            return member.getMemberId();

        } else if (this.socialMember != null) {
            return socialMember.getSocialName();

        } else {
            throw new IllegalStateException("Member 또는 SocialMember 정보가 없습니다.");
        }
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

    @Override
    public String getName() {
        return null;
    }
}
