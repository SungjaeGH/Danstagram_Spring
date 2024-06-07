package com.project.danstagram.domain.comment.dto;

import com.project.danstagram.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long commentIdx;

    public static CommentResponseDto toResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .commentIdx(comment.getCommentIdx())
                .build();
    }
}
