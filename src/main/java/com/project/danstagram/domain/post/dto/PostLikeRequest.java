package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.entity.PostLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PostLikeRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdatePostLike {

        private Long postIdx;
        private String writerId;
        private Long postLikeIdx;
        private final boolean isPostLike;

        public void setPostLikeIdx(Long postLikeIdx) {
            this.postLikeIdx = postLikeIdx;
        }

        public UpdatePostLike appendDto(Long postIdx, String writerId) {
            return UpdatePostLike.builder()
                        .postIdx(postIdx)
                        .writerId(writerId)
                        .isPostLike(this.isPostLike)
                    .build();
        }

        public PostLike toEntity(Post post, Member member) {
            return PostLike.builder()
                    .postLikeIdx(this.postLikeIdx)
                    .postLikeStatus(this.isPostLike)
                    .post(post)
                    .member(member)
                    .build();
        }
    }
}
