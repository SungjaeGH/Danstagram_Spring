package com.project.danstagram.domain.comment.dto;

import com.project.danstagram.domain.comment.entity.Comment;
import lombok.*;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class CreateCommentDto {
    private Long commentParentIdx;
    private String commentContent;

    @Builder
    public Comment toEntity(String currDate) {
        return Comment.builder()
                .commentContent(this.commentContent)
                .commentDate(currDate)
                .commentLikes(new ArrayList<>())
                .build();
    }
}
