package com.project.danstagram.domain.follow.dto;

import com.project.danstagram.domain.follow.entity.Follow;
import com.project.danstagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateFollowDto {
    private Long followIdx;
    private String followRole;
    private boolean isFollower;

    public void setFollowIdx(Long followIdx) {
        this.followIdx = followIdx;
    }

    @Builder
    public Follow toEntity(Member toUser, Member fromUser) {
        return Follow.builder()
                .followIdx(this.followIdx)
                .followRole("test")
                .followDeleteStatus(this.isFollower)
                .followToUser(toUser)
                .followFromUser(fromUser)
                .build();
    }
}
