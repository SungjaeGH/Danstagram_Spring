package com.project.danstagram.domain.member.dto;

import com.project.danstagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDto {

    private final String memberId;
    private String memberImg;
    private String memberName;
    private String memberIntroduce;
    private Long postCount;
    private Long followerCount;
    private Long followingCount;

    public static ProfileResponseDto toResponseDto(Member member) {
        return ProfileResponseDto.builder()
                .memberId(member.getMemberName())
                .build();
    }
}
