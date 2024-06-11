package com.project.danstagram.domain.post.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "postLikeIdx")
@Table(name = "post_like")
@Entity
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_idx", updatable = false, unique = true, nullable = false)
    private Long postLikeIdx;

    @Column(name = "post_like_status", columnDefinition = "tinyint(1) default 0")
    private boolean postLikeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_idx", referencedColumnName = "post_idx")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_like_member_idx", referencedColumnName = "member_idx")
    private Member member;

    @Builder
    public PostLike(Long postLikeIdx, boolean postLikeStatus, Post post, Member member) {
        this.postLikeIdx = postLikeIdx;
        this.postLikeStatus = postLikeStatus;
        this.post = post;
        this.member = member;
    }

    public void putPost(Post post) {
        this.post = post;
    }

    public void putMember(Member member) {
        this.member = member;
    }
}
