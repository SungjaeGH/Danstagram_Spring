package com.project.danstagram.domain.member.dto;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;

public class MemberRequest {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SignUp {

        @NotBlank(message = "이메일 또는 휴대번호는 필수 입력 값입니다.")
        private String memberInfo;

        @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$",
                message = "이메일 형식이 올바르지 않습니다.")
        private String memberEmail;

        @Pattern(regexp = "^(\\d{3})-(\\d{3,4})-(\\d{4})$",
                message = "휴대번호 형식이 올바르지 않습니다.")
        private String memberPhone;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String memberName;

        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        private String memberId;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
                message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String memberPw;

        private String socialMemberEmail;

        public Member toEntity(String encodedPassword) {
            return Member.builder()
                    .memberEmail(memberEmail)
                    .memberPhone(memberPhone)
                    .memberName(memberName)
                    .memberId(memberId)
                    .memberPw(encodedPassword)
                    .memberRole(Role.USER)
                    .socialMembers(new ArrayList<>())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResetPw {

        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
                message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String newMemberPw;
        private String confirmMemberPw;
        private String memberId;

        public ResetPw appendDto(String memberId) {
            return ResetPw.builder()
                    .newMemberPw(this.newMemberPw)
                    .confirmMemberPw(this.confirmMemberPw)
                    .memberId(memberId)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdateProfile {

        private final String memberWebsite;
        private final String memberIntroduce;
        private final String memberGender;
        private String memberId;

        public UpdateProfile appendDto(String memberId) {
            return UpdateProfile.builder()
                    .memberWebsite(this.memberWebsite)
                    .memberIntroduce(this.memberIntroduce)
                    .memberGender(this.memberGender)
                    .memberId(memberId)
                    .build();
        }
    }

    @Builder
    public record DisplayMain(String memberId, int postScrollSize, Long lastPostIdx, int storyScrollSize, Long lastStoryIdx) {
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdateDeleteStatus {

        private String memberId;
        private final boolean isMemberDelete;

        public UpdateDeleteStatus appendDto(String memberId) {
            return UpdateDeleteStatus.builder()
                    .memberId(memberId)
                    .isMemberDelete(this.isMemberDelete)
                    .build();
        }
    }
}
