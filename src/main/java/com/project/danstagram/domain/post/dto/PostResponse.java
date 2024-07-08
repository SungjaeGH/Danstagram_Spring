package com.project.danstagram.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PostResponse {

    @Builder
    public record CreatePost(Long postIdx) {
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FindPost {

        private String postContent;
        private String postDate;
        private String postUpdateDate;
        private String postDeleteDate;
        private List<PostImageResponse.PostImageInfo> postImageList;
    }

    @Builder
    public record UpdatePost(Long postIdx, boolean isPostUpdate, String postUpdateDate) {
    }

    @Builder
    public record PostListForProfile(Long totalElements, Long nextCursor, List<PostInfoForProfile> contents) {
    }

    @Getter
    public static class PostInfoForProfile {

        private final Long postIdx;
        private final Long postLikeCount;
        private String postImg;
        private Long postCommentCount;

        @QueryProjection
        public PostInfoForProfile(Long postIdx, Long postLikeCount) {
            this.postIdx = postIdx;
            this.postLikeCount = postLikeCount;
        }

        @Builder
        public PostInfoForProfile(Long postIdx, Long postLikeCount, String postImg, Long postCommentCount) {
            this.postIdx = postIdx;
            this.postLikeCount = postLikeCount;
            this.postImg = postImg;
            this.postCommentCount = postCommentCount;
        }
    }

    @Builder
    public record UpdatePostDeleteStatus(Long postIdx, boolean isPostDelete, String postDeleteDate) {
    }
}