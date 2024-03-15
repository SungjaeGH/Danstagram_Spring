package com.project.danstagram.domain.auth.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "socialIdx")
@Table(name = "social_member")
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

    @ManyToOne
    @JoinColumn(name = "member_idx", referencedColumnName = "member_idx")
    private Member member;
}
