package com.project.danstagram.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {
    private Long postIdx;
    private String postContent;
    private String postDate;
    private String postUpdateDate;
    private String postDeleteDate;
    private List<PostImageResponseDto> postImageList;

    @Builder(builderMethodName = "createPostBuilder", buildMethodName = "createPostBuild")
    public PostResponseDto(Long postIdx) {
        this.postIdx = postIdx;
    }

    @Builder(builderMethodName = "findPostBuilder", buildMethodName = "findPostBuild")
    public PostResponseDto(String postContent, String postDate, String postUpdateDate, String postDeleteDate,
                           List<PostImageResponseDto> postImageList) {
        this.postContent = postContent;
        this.postDate = postDate;
        this.postUpdateDate = postUpdateDate;
        this.postDeleteDate = postDeleteDate;
        this.postImageList = postImageList;
    }
}
