package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long postIdx;

    @Builder
    public static PostResponseDto toResponseDto(Post post) {
        return new PostResponseDto(post.getPostIdx());
    }
}
