package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.post.entity.Post;
import lombok.*;

@Getter
@AllArgsConstructor
public class CreatePostDto {
    private Long writerIdx;
    private String postContent;

    @Builder
    public Post toEntity(String currDate) {
        return Post.builder()
                .postContent(postContent)
                .postDate(currDate)
                .build();
    }
}
