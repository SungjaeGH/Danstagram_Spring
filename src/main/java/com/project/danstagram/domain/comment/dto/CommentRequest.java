package com.project.danstagram.domain.comment.dto;

import com.project.danstagram.domain.comment.entity.Comment;
import com.project.danstagram.domain.comment.entity.CommentLike;
import com.project.danstagram.domain.member.entity.Member;
import lombok.*;

import java.util.ArrayList;

public class CommentRequest {

    public record CreateComment(Long commentParentIdx, String commentContent) {

        public Comment toEntity(String currDate) {
            return Comment.builder()
                    .commentContent(this.commentContent)
                    .commentDate(currDate)
                    .commentLikes(new ArrayList<>())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class UpdateCommentLike {

        private Long commentLikeIdx;
        private final boolean isCommentLike;

        public void setCommentLikeIdx(Long commentLikeIdx) {
            this.commentLikeIdx = commentLikeIdx;
        }

        public CommentLike toEntity(Comment comment, Member member) {
            return CommentLike.builder()
                    .commentLikeIdx(this.commentLikeIdx)
                    .commentLikeStatus(this.isCommentLike)
                    .comment(comment)
                    .member(member)
                    .build();
        }
    }

    public record FindComments(Long postIdx, int scrollSize, Long lastCommentIdx) {

    }
}
