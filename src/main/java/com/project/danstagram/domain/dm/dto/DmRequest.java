package com.project.danstagram.domain.dm.dto;

import com.project.danstagram.domain.dm.entity.Dm;
import com.project.danstagram.domain.dm.entity.DmGroup;
import com.project.danstagram.domain.dm.entity.DmGroupMember;
import com.project.danstagram.domain.dm.entity.DmLike;
import com.project.danstagram.domain.member.entity.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class DmRequest {

    @Getter
    public static class CreateDmGroup {
        List<String> targetMembers = new ArrayList<>();

        public static DmGroup toEntity(Member leaderMember, String dmGroupName) {
            return DmGroup.builder()
                    .dmGroupName(dmGroupName)
                    .member(leaderMember)
                    .dmGroupMembers(new ArrayList<>())
                    .build();
        }
    }

    @Getter
    public static class CreateDmGroupMember {

        public static DmGroupMember toEntity(Member member) {
            return DmGroupMember.builder()
                    .member(member)
                    .build();
        }
    }

    public record CreateDmMessage(String dmMessage, String writerId) {

        public Dm toEntity(String currDate) {
            return Dm.builder()
                    .dmMessage(this.dmMessage)
                    .dmDate(currDate)
                    .dmLikes(new ArrayList<>())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DmMessageScrollInfo {

        private final int scrollSize;
        private Long lastDmIdx;
    }

    @Getter
    @Setter
    public static class UpdateDmLike {

        private Long dmLikeIdx;
        private Long dmGroupIdx;
        private Long dmIdx;
        private String memberId;
        private final boolean isDmLike;

        public UpdateDmLike(boolean isDmLike) {
            this.isDmLike = isDmLike;
        }

        public DmLike toEntity(Dm dm, Member member) {
            return DmLike.builder()
                    .dmLikeIdx(this.dmLikeIdx)
                    .dmLikeStatue(this.isDmLike)
                    .dm(dm)
                    .member(member)
                    .build();
        }
    }
}
