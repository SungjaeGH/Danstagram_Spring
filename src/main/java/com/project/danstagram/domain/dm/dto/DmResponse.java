package com.project.danstagram.domain.dm.dto;

import com.project.danstagram.domain.dm.entity.Dm;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class DmResponse {

    @Builder
    public record CreateDmGroup(Long dmGroupIdx) {
    }

    @Builder
    public record DmGroupList(Long totalElements, Long nextCursor, List<DmGroupInfo> dmGroupList) {
    }

    @Getter
    public static class DmGroupInfo {

        private final Long dmGroupIdx;
        private final String dmGroupName;
        private Map<String, DmResponse.DmGroupMemberInfo> dmMembers;
        private LastDmMessage lastDmMsg;

        @QueryProjection
        public DmGroupInfo(Long dmGroupIdx, String dmGroupName) {
            this.dmGroupIdx = dmGroupIdx;
            this.dmGroupName = dmGroupName;
        }

        public void setDmMembers(Map<String, DmResponse.DmGroupMemberInfo> dmMembers) {
            this.dmMembers = dmMembers;
        }

        public void setLastDmMsg(LastDmMessage lastDmMsg) {
            this.lastDmMsg = lastDmMsg;
        }
    }

    @Getter
    @Builder
    public static class DmGroupMemberInfo {

        private String memberId;
        private String profileImg;
        private boolean isLogin;
        private String memberLogoutDate;
        private boolean isLeader;

        @QueryProjection
        public DmGroupMemberInfo(String memberId, String profileImg, boolean isLogin, String memberLogoutDate, boolean isLeader) {
            this.memberId = memberId;
            this.profileImg = profileImg;
            this.isLogin = isLogin;
            this.memberLogoutDate = memberLogoutDate;
            this.isLeader = isLeader;
        }

        public void setProfileImg(String profileImg) {
            this.profileImg = profileImg;
        }
    }

    @Getter
    @Builder
    public static class LastDmMessage {

        private String dmMessage;
        private String dmDate;

        @QueryProjection
        public LastDmMessage(String dmMessage, String dmDate) {
            this.dmMessage = dmMessage;
            this.dmDate = dmDate;
        }
    }

    @Builder
    public record SendMessage(Long dmIdx, String writerId, String dmMessage) {
    }

    @Builder
    public record DmMsgList(Long totalElements, Long nextCursor, List<DmMsgInfo> dmMsgs) {
    }

    public record DmMsgInfo(Long dmIdx, String writerId, String dmMessage, String dmDate, List<String> dmLikes) {

        @QueryProjection
        public DmMsgInfo {

        }
    }

    @Builder
    public record DeleteDmMsg(Long dmIdx, String dmMessage) {

        public static DeleteDmMsg toResponse(Dm dm) {
            return DeleteDmMsg.builder()
                    .dmIdx(dm.getDmIdx())
                    .dmMessage(dm.getDmMessage())
                    .build();
        }
    }

    @Builder
    public record UpdateDmLike(Long dmGroupIdx, Long dmIdx, String dmMessage, boolean isDmLike) {
    }
}
