package com.project.danstagram.domain.comment.dto;

import com.project.danstagram.domain.comment.entity.Comment;
import com.project.danstagram.domain.comment.entity.CommentLike;
import com.project.danstagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCommentLikeDto {
    private Long commentLikeIdx;
    private boolean isCommentLike;

    public void setCommentLikeIdx(Long commentLikeIdx) {
        this.commentLikeIdx = commentLikeIdx;
    }

    @Builder
    public CommentLike toEntity(Comment comment, Member member) {
        return CommentLike.builder()
                .commentLikeIdx(this.commentLikeIdx)
                .commentLikeStatus(this.isCommentLike)
                .comment(comment)
                .member(member)
                .build();
    }
}
