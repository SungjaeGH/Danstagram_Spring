package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.entity.PostLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePostLikeDto {
    private Long postLikeIdx;
    private boolean isPostLike;

    public void setPostLikeIdx(Long postLikeIdx) {
        this.postLikeIdx = postLikeIdx;
    }

    @Builder
    public PostLike toEntity(Post post, Member member) {
        return PostLike.builder()
                .postLikeIdx(this.postLikeIdx)
                .postLikeStatus(this.isPostLike)
                .post(post)
                .member(member)
                .build();
    }
}
