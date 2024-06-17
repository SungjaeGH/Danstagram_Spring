package com.project.danstagram.domain.member.service;

import com.project.danstagram.domain.follow.repository.FollowRepository;
import com.project.danstagram.domain.member.dto.*;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.entity.SocialMember;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.member.repository.SocialMemberRepository;
import com.project.danstagram.domain.post.repository.PostRepository;
import com.project.danstagram.global.file.ConstUtil;
import com.project.danstagram.global.file.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public MemberResponseDto signUp(SignUpDto signUpDto) {
        if (memberRepository.existsByMemberId(signUpDto.getMemberId())) {
            throw new IllegalArgumentException("이미 사용중인 사용자 이름입니다.");
        }

        // 정규식 체크
        int checkCode = checkMemberInfoValidation(signUpDto.getMemberInfo());
        switch (checkCode) {
            case 1:
                signUpDto.setMemberEmail(signUpDto.getMemberInfo());
                break;

            case 2:
                signUpDto.setMemberPhone(signUpDto.getMemberInfo());
                break;

            default:
                throw new IllegalArgumentException("올바른 이메일 또는 휴대전화 형식이 아닙니다.");
        }

        String encodePw = passwordEncoder.encode(signUpDto.getMemberPw());

        Member savedMember = signUpDto.toEntity(encodePw);

        // 소셜 회원일 경우, 1:N 연결
        if (signUpDto.getSocialMemberEmail() != null) {
            SocialMember socialMember = socialMemberRepository
                    .findBySocialEmail(signUpDto.getSocialMemberEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 소셜 회원을 찾을 수 없습니다."));

            savedMember.putSocialMember(socialMember);
        }

        return MemberResponseDto.toResponseDto(memberRepository.save(savedMember));
    }

    private static int checkMemberInfoValidation(String memberInfo) {
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        String phonePattern = "^(\\d{3})-(\\d{3,4})-(\\d{4})$";
        int resultCode = 0;

        if (Pattern.matches(emailPattern, memberInfo)) {
            resultCode = 1;

        } else if (Pattern.matches(phonePattern, memberInfo)) {
            resultCode = 2;
        }

        return resultCode;
    }

    @Override
    public MemberResponseDto findMember(String memberInfo) {
        Member member = memberRepository.findByMemberIdOrMemberPhoneOrMemberEmail(memberInfo, memberInfo, memberInfo)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        return MemberResponseDto.toResponseDto(member);
    }

    @Override
    public MemberResponseDto resetMemberPw(String memberId, ResetPwDto resetPwDto) {
        if (!resetPwDto.getNewMemberPw().equals(resetPwDto.getConfirmMemberPw())) {
            throw new IllegalArgumentException("입력한 비밀번호가 일치하지 않습니다.");
        }

        Member changedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        String encodedNewPw = passwordEncoder.encode(resetPwDto.getNewMemberPw());
        changedMember.changePw(encodedNewPw);

        return MemberResponseDto.toResponseDto(memberRepository.save(changedMember));
    }

    @Override
    public ProfileResponseDto updateProfile(String memberId, UpdateProfileDto updateProfileDto) {

        Member updatedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        updatedMember.updateProfile(updateProfileDto);

        return ProfileResponseDto.toResponseDto(memberRepository.save(updatedMember));
    }

    @Override
    public ProfileResponseDto updateProfileImg(String memberId, MultipartFile imgFile) throws IOException {

        Member updatedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        int fileFlag = ConstUtil.UPLOAD_IMAGE_FLAG;

        // 이미지를 업로드할 디렉토리 존재 유무 확인
        fileUploadUtil.checkDirectory(fileFlag, true);

        // 변경할 이미지 정보 세팅
        Map<String, Object> profileImg = fileUploadUtil.singleFileUpload(updatedMember.getMemberIdx(), fileFlag, imgFile);
        updatedMember.updateProfileImg(profileImg);

        return ProfileResponseDto.toResponseDto(memberRepository.save(updatedMember));
    }

    @Override
    public ProfileResponseDto displayProfile(String memberId) {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        return ProfileResponseDto.builder()
                    .memberId(memberId)
                    .memberImg(getProfileImg(member.getMemberStoreImage()))
                    .memberName(member.getMemberName())
                    .memberIntroduce(member.getMemberIntroduce())
                    .postCount(postRepository.countPostByWriter(member.getMemberIdx()))
                    .followerCount(followRepository.countFollowers(member.getMemberIdx()))
                    .followingCount(followRepository.countFollowings(member.getMemberIdx()))
                .build();
    }

    private String getProfileImg(String imgName) {

        if (imgName == null) {
            return null;
        }

        String uploadPath = fileUploadUtil.getUploadPath(ConstUtil.UPLOAD_IMAGE_FLAG);
        File imageFile = new File(uploadPath, imgName);

        return fileUploadUtil.getFileEncoding(imageFile);
    }
}