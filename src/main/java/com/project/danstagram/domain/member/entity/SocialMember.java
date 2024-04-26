package com.project.danstagram.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "socialIdx")
@Table(name = "social_member")
@Entity
public class SocialMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_idx", updatable = false, unique = true, nullable = false)
    private Long socialIdx;

    @Column(name = "social_email", unique = true, nullable = false)
    private String socialEmail;

    @Column(name = "provider_id", unique = true, nullable = false)
    private String providerId;

    @Column(name = "provider", unique = true, nullable = false)
    private String provider;

    @Column(name = "social_name", unique = true, nullable = false)
    private String socialName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", referencedColumnName = "member_idx")
    private Member member;

    @Builder
    public SocialMember(Long socialIdx, String socialEmail, String providerId, String provider, String socialName, Member member) {
        this.socialIdx = socialIdx;
        this.socialEmail = socialEmail;
        this.providerId = providerId;
        this.provider = provider;
        this.socialName = socialName;
        this.member = member;
    }

    public void putMember(Member member) {
        this.member = member;
    }
}
