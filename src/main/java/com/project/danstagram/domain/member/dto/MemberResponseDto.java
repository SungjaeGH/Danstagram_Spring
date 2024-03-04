package com.project.danstagram.domain.member.dto;

import com.project.danstagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long memberIdx;

    public static MemberResponseDto toResponseDto(Member member) {
        return MemberResponseDto.builder()
                .memberIdx(member.getMemberIdx())
                .build();
    }
}
