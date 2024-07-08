package com.project.danstagram.domain.comment.dto;

import com.project.danstagram.domain.comment.entity.Comment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CommentResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateComment {

        private Long commentIdx;

        public static CreateComment toResponseDto(Comment comment) {
            return CreateComment.builder()
                    .commentIdx(comment.getCommentIdx())
                    .build();
        }
    }

    @Builder
    public record UpdateCommentLike(Long postIdx, Long commentIdx, String memberId,
                                    Long commentLikeIdx, boolean isCommentLike) {
    }

    @Builder
    public record CommentList(Long totalElements, Long nextCursor, List<CommentInfo> commentInfos){
    }

    @Getter
    public static class CommentInfo {

        private final Long commentIdx;
        private final String commentContent;
        private final String commentDate;
        private final String writerId;
        private final Long commentLikeCount;
        private String writerImg;
        private List<CommentInfo> childCommentInfos;

        @QueryProjection
        public CommentInfo(Long commentIdx, String commentContent, String commentDate, String writerId, String writerImg, Long commentLikeCount) {
            this.commentIdx = commentIdx;
            this.commentContent = commentContent;
            this.commentDate = commentDate;
            this.writerId = writerId;
            this.writerImg = writerImg;
            this.commentLikeCount = commentLikeCount;
        }

        public void setWriterImg(String writerImg) {
            this.writerImg = writerImg;
        }

        public void setChildCommentInfos(List<CommentInfo> childCommentInfos) {
            this.childCommentInfos = childCommentInfos;
        }
    }
}
