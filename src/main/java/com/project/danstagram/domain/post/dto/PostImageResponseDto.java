package com.project.danstagram.domain.post.dto;

import lombok.Getter;

@Getter
public class PostImageResponseDto {
    private final Long postImageIdx;
    private final String storeImageFile;

    public PostImageResponseDto(Long postImageIdx, String storeImageFile) {
        this.postImageIdx = postImageIdx;
        this.storeImageFile = storeImageFile;
    }
}
