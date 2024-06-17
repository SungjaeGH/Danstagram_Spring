package com.project.danstagram.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class PostImageResponseDto {

    private final Long postImageIdx;
    private final String imageName;
    private final String encodingImage;
}
