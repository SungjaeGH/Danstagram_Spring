package com.project.danstagram.domain.member.entity;

import com.project.danstagram.domain.auth.entity.SocialMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "memberIdx")
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Enumerated(EnumType.STRING)
    private Role memberRole;

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
    private LocalDateTime memberLoginDate;

    @Column(name = "member_logout_date")
    private LocalDateTime memberLogoutDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialMember> socialMembers = new ArrayList<>();

    public void putSocialMember(SocialMember socialMember) {
        this.socialMembers.add(socialMember);
    }
}