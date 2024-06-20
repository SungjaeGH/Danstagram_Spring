package com.project.danstagram.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostInfoResponseDto {

    private final Long postIdx;
    private final String postFirstEncodingImg;
    private final Long postLikeCount;
    private final Long postCommentCount;

    @Builder
    public PostInfoResponseDto(Long postIdx, String postFirstEncodingImg, Long postLikeCount, Long postCommentCount) {
        this.postIdx = postIdx;
        this.postFirstEncodingImg = postFirstEncodingImg;
        this.postLikeCount = postLikeCount;
        this.postCommentCount = postCommentCount;
    }
}
