package com.project.danstagram.domain.member.entity;

import com.project.danstagram.domain.comment.entity.Comment;
import com.project.danstagram.domain.comment.entity.CommentLike;
import com.project.danstagram.domain.dm.entity.Dm;
import com.project.danstagram.domain.dm.entity.DmGroup;
import com.project.danstagram.domain.dm.entity.DmGroupMember;
import com.project.danstagram.domain.dm.entity.DmLike;
import com.project.danstagram.domain.follow.entity.Follow;
import com.project.danstagram.domain.member.dto.UpdateProfileDto;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.search.entity.Search;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Column(name = "member_store_image")
    private String memberStoreImage;

    @Column(name = "member_upload_image")
    private String memberUploadImage;

    @Column(name = "member_status")
    private String memberStatus;

    @Column(name = "member_login_date")
    private String memberLoginDate;

    @Column(name = "member_logout_date")
    private String memberLogoutDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialMember> socialMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "followToUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followToUsers = new ArrayList<>();

    @OneToMany(mappedBy = "followFromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followFromUsers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DmGroup> dmGroups = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DmGroupMember> dmGroupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dm> dms = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DmLike> dmLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Search> searches = new ArrayList<>();

    @Builder
    public Member(Long memberIdx, String memberId, String memberPhone, String memberEmail, String memberPw, Role memberRole, String memberName, String memberIntroduce, String memberWebsite, String memberGender, String memberStoreImage, String memberUploadImage, String memberStatus, String memberLoginDate, String memberLogoutDate, List<SocialMember> socialMembers, List<Post> posts, List<Comment> comments, List<CommentLike> commentLikes, List<Follow> followToUsers, List<Follow> followFromUsers, List<DmGroup> dmGroups, List<DmGroupMember> dmGroupMembers, List<Dm> dms, List<DmLike> dmLikes, List<Search> searches) {
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
        this.memberStoreImage = memberStoreImage;
        this.memberUploadImage = memberUploadImage;
        this.memberStatus = memberStatus;
        this.memberLoginDate = memberLoginDate;
        this.memberLogoutDate = memberLogoutDate;
        this.socialMembers = socialMembers;
        this.posts = posts;
        this.comments = comments;
        this.commentLikes = commentLikes;
        this.followToUsers = followToUsers;
        this.followFromUsers = followFromUsers;
        this.dmGroups = dmGroups;
        this.dmGroupMembers = dmGroupMembers;
        this.dms = dms;
        this.dmLikes = dmLikes;
        this.searches = searches;
    }

    public void putSocialMember(SocialMember socialMember) {
        this.socialMembers.add(socialMember);
        socialMember.putMember(this);
    }

    public void putPost(Post post) {
        this.posts.add(post);
        post.putMember(this);
    }

    public void putComment(Comment comment) {
        this.comments.add(comment);
        comment.putMember(this);
    }

    public void putCommentLike(CommentLike commentLike) {
        this.commentLikes.add(commentLike);
        commentLike.putMember(this);
    }

    public void putFollowToUsers(Follow followToUser) {
        this.followToUsers.add(followToUser);
        followToUser.putFollowToUser(this);
    }

    public void putDmGroups(DmGroup dmGroup) {
        this.dmGroups.add(dmGroup);
        dmGroup.putMember(this);
    }

    public void putDmGroupMember(DmGroupMember dmGroupMember) {
        this.dmGroupMembers.add(dmGroupMember);
        dmGroupMember.putMember(this);
    }

    public void putDm(Dm dm) {
        this.dms.add(dm);
        dm.putMember(this);
    }

    public void putDmLike(DmLike dmLike) {
        this.dmLikes.add(dmLike);
        dmLike.putMember(this);
    }

    public void putSearch(Search search) {
        this.searches.add(search);
        search.putMember(this);
    }

    public void changePw(String newEncodedPw) {
        this.memberPw = newEncodedPw;
    }

    public void updateProfile(UpdateProfileDto updateProfileDto) {
        Optional.ofNullable(updateProfileDto.getMemberWebsite())
                .ifPresent(website -> this.memberWebsite = website);
        Optional.ofNullable(updateProfileDto.getMemberIntroduce())
                .ifPresent(introduce -> this.memberIntroduce = introduce);
        Optional.ofNullable(updateProfileDto.getMemberGender())
                .ifPresent(gender -> this.memberGender = gender);
    }

    public void updateProfileImg(Map<String, Object> profileImg) {
        if (profileImg.get("empty").equals("yes")) {
            this.memberStoreImage = null;
            this.memberUploadImage = null;

        } else {
            this.memberStoreImage = (String) profileImg.get("fileName");
            this.memberUploadImage = (String) profileImg.get("originalFileName");
        }
    }

    public void changeLoginState(String currTime) {
        this.memberLoginDate = currTime;
        this.memberStatus = "Login";
    }

    public void changeLogoutState(String currTime) {
        this.memberLogoutDate = currTime;
        this.memberStatus = "Logout";
    }
}