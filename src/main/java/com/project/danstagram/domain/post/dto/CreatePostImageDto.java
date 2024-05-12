package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.post.entity.PostImage;
import lombok.*;

@Getter
@AllArgsConstructor
public class CreatePostImageDto {
    private String fileName;
    private String originalFileName;

    @Builder
    public PostImage toEntity() {
        return PostImage.builder()
                .storeImageFile(fileName)
                .uploadImageFile(originalFileName)
                .build();
    }
}
