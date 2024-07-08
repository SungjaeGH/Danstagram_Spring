package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.post.entity.PostImage;
import lombok.Builder;

public class PostImageResponse {

    public record CreatePostImage(String fileName, String originalFileName) {

        @Builder
        public static PostImage toEntity(String fileName, String originalFileName) {
            return PostImage.builder()
                    .storeImageFile(fileName)
                    .uploadImageFile(originalFileName)
                    .build();
        }
    }

    @Builder
    public record PostImageInfo(Long postImageIdx, String imageName, String encodingImage) {
    }
}
