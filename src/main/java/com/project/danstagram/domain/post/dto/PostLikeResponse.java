package com.project.danstagram.domain.post.dto;

import lombok.Builder;

public class PostLikeResponse {

    @Builder
    public record UpdatePostLike(Long postIdx, String memberId, boolean isPostLike) {
    }
}
