package com.project.danstagram.domain.auth.entity;

import com.project.danstagram.domain.member.entity.Member;
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
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return String.valueOf(member.getMemberRole());
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return member.getMemberPw();
    }

    @Override
    public String getUsername() {
        return member.getMemberId();
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
