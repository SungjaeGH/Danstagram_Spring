package com.project.danstagram.domain.member.dto;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.post.dto.PostImageResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class MemberResponse {

    @Builder
    public record SignUp(String memberName) {

        public static SignUp toResponse(Member member) {
            return SignUp.builder()
                    .memberName(member.getMemberName())
                    .build();
        }
    }

    @Builder
    public record ResetPw(String memberId, boolean isPwReset) {
    }

    @Builder
    public record UpdateProfile(String memberId, boolean isProfileUpdate) {
    }

    @Builder
    public record UpdateProfileImg(String memberId, boolean isProfileImgUpdate) {
    }

    @Builder
    @RequiredArgsConstructor
    public static class DisplayProfile {

        private final String memberId;
        private final String memberImg;
        private final String memberName;
        private final String memberIntroduce;
        private final Long postCount;
        private final Long followerCount;
        private final Long followingCount;
    }

    @Builder
    public record DisplayMainList(DisplayMemberMain memberInfo, DisplayPostMainList postInfo) {

    }

    @Builder
    public record DisplayMemberMain(String memberId, String memberName, String memberProfile) {
    }

    @Builder
    public record DisplayPostMainList(Long totalElements, Long nextCursor, List<DisplayPostMain> contents) {
    }

    @Getter
    public static class DisplayPostMain {

        private final Long postIdx;
        private final String writerId;
        private final String postContent;
        private final String postDate;
        private final String postUpdateDate;
        private final Long postLikeCount;
        private List<PostImageResponse.PostImageInfo> postImageList;

        @QueryProjection
        public DisplayPostMain(Long postIdx, String writerId, String postContent, String postDate, String postUpdateDate, Long postLikeCount) {
            this.postIdx = postIdx;
            this.writerId = writerId;
            this.postContent = postContent;
            this.postDate = postDate;
            this.postUpdateDate = postUpdateDate;
            this.postLikeCount = postLikeCount;
        }

        public void setPostImageList(List<PostImageResponse.PostImageInfo> postImageList) {
            this.postImageList = postImageList;
        }
    }

    @Builder
    public record UpdateDeleteStatus(String memberId, boolean isMemberDelete, String memberDeleteDate) {
    }
}
