package com.project.danstagram.domain.auth.entity;

import com.project.danstagram.domain.member.entity.Member;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SecurityMemberDto {

    private Long memberIdx;
    private String memberId;
    private String memberRole;

    public SecurityMemberDto(Long memberIdx, String memberId, String memberRole) {
        this.memberIdx = memberIdx;
        this.memberId = memberId;
        this.memberRole = memberRole;
    }

    public static SecurityMemberDto toEntity(Member member) {
        return SecurityMemberDto.builder()
                .memberIdx(member.getMemberIdx())
                .memberId(member.getMemberId())
                .memberRole("ROLE_".concat(member.getMemberRole().toString()))
                .build();
    }
}

