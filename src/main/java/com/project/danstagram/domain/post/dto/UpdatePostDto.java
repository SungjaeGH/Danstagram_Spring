package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdatePostDto {
    private String updateContent;

    public Post toEntity(String updateDate, Post targetPost) {
        return Post.builder()
                .postIdx(targetPost.getPostIdx())
                .postContent(this.updateContent)
                .postDate(targetPost.getPostDate())
                .postUpdateDate(updateDate)
                .postDeleteDate(targetPost.getPostDeleteDate())
                .member(targetPost.getMember())
                .postImages(targetPost.getPostImages())
                .build();
    }
}
