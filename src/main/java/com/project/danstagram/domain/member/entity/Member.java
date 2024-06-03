package com.project.danstagram.domain.member.entity;

import com.project.danstagram.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "memberIdx")
@Table(name = "member")
@Entity
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(Long memberIdx, String memberId, String memberPhone, String memberEmail, String memberPw, Role memberRole, String memberName, String memberIntroduce, String memberWebsite, String memberGender, String memberImageUrl, String memberImageType, String memberImageName, String memberImageUuid, String memberStatus, LocalDateTime memberLoginDate, LocalDateTime memberLogoutDate, List<SocialMember> socialMembers) {
        this.memberIdx = memberIdx;
        this.memberId = memberId;
        this.memberPhone = memberPhone;
        this.memberEmail = memberEmail;
        this.memberPw = memberPw;
        this.memberRole = memberRole;
        this.memberName = memberName;
        this.memberIntroduce = memberIntroduce;
        this.memberWebsite = memberWebsite;
        this.memberGender = memberGender;
        this.memberImageUrl = memberImageUrl;
        this.memberImageType = memberImageType;
        this.memberImageName = memberImageName;
        this.memberImageUuid = memberImageUuid;
        this.memberStatus = memberStatus;
        this.memberLoginDate = memberLoginDate;
        this.memberLogoutDate = memberLogoutDate;
        this.socialMembers = socialMembers;
    }

    public void putSocialMember(SocialMember socialMember) {
        this.socialMembers.add(socialMember);
        socialMember.putMember(this);
    }

    public void putPost(Post post) {
        this.posts.add(post);
        post.putMember(this);
    }

    public void changePw(String newEncodedPw) {
        this.memberPw = newEncodedPw;
    }
}