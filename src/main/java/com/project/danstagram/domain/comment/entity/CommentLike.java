package com.project.danstagram.domain.comment.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "commentLikeIdx")
@Table(name = "comment_like")
@Entity
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_idx", updatable = false, unique = true, nullable = false)
    private Long commentLikeIdx;

    @Column(name = "comment_like_status", columnDefinition = "tinyint(1) default 0")
    private boolean commentLikeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_idx", referencedColumnName = "comment_idx")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_like_member_idx", referencedColumnName = "member_idx")
    private Member member;

    @Builder
    public CommentLike(Long commentLikeIdx, boolean commentLikeStatus, Comment comment, Member member) {
        this.commentLikeIdx = commentLikeIdx;
        this.commentLikeStatus = commentLikeStatus;
        this.comment = comment;
        this.member = member;
    }

    public void putComment(Comment comment) {
        this.comment = comment;
    }

    public void putMember(Member member) {
        this.member = member;
    }
}
