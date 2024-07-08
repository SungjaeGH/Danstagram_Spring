package com.project.danstagram.domain.member.service;

import com.project.danstagram.domain.follow.repository.FollowRepository;
import com.project.danstagram.domain.member.dto.*;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.entity.SocialMember;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.member.repository.SocialMemberRepository;
import com.project.danstagram.domain.post.repository.PostRepository;
import com.project.danstagram.domain.post.repository.PostRepositoryCustom;
import com.project.danstagram.domain.post.service.PostImageService;
import com.project.danstagram.global.file.ConstUtil;
import com.project.danstagram.global.file.FileUploadUtil;
import com.project.danstagram.global.scroll.PageRequestUtil;
import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final PostImageService postImageService;
    private final MemberRepository memberRepository;
    private final SocialMemberRepository socialMemberRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadUtil fileUploadUtil;
    private final TimeUtil timeUtil;

    public MemberResponse.SignUp signUp(MemberRequest.SignUp request) {
        if (memberRepository.existsByMemberId(request.getMemberId())) {
            throw new IllegalArgumentException("이미 사용중인 사용자 이름입니다.");
        }

        // 정규식 체크
        int checkCode = checkMemberInfoValidation(request.getMemberInfo());
        switch (checkCode) {
            case 1:
                request.setMemberEmail(request.getMemberInfo());
                break;

            case 2:
                request.setMemberPhone(request.getMemberInfo());
                break;

            default:
                throw new IllegalArgumentException("올바른 이메일 또는 휴대전화 형식이 아닙니다.");
        }

        String encodePw = passwordEncoder.encode(request.getMemberPw());

        Member savedMember = request.toEntity(encodePw);

        // 소셜 회원일 경우, 1:N 연결
        if (request.getSocialMemberEmail() != null) {
            SocialMember socialMember = socialMemberRepository
                    .findBySocialEmail(request.getSocialMemberEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 소셜 회원을 찾을 수 없습니다."));

            savedMember.putSocialMember(socialMember);
        }

        return MemberResponse.SignUp.toResponse(memberRepository.save(savedMember));
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

    public MemberResponse.ResetPw resetMemberPw(MemberRequest.ResetPw request) {

        boolean isPwReset = true;

        if (!request.getNewMemberPw().equals(request.getConfirmMemberPw())) {
            throw new IllegalArgumentException("입력한 비밀번호가 일치하지 않습니다.");
        }

        Member changedMember = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        String encodedNewPw = passwordEncoder.encode(request.getNewMemberPw());
        changedMember.changePw(encodedNewPw);

        Member saved = memberRepository.save(changedMember);

        return MemberResponse.ResetPw.builder()
                .memberId(saved.getMemberId())
                .isPwReset(isPwReset)
                .build();
    }

    public MemberResponse.UpdateProfile updateProfile(MemberRequest.UpdateProfile request) {

        boolean isProfileUpdate = true;

        Member updatedMember = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        updatedMember.updateProfile(request);

        Member saved = memberRepository.save(updatedMember);

        return MemberResponse.UpdateProfile.builder()
                .memberId(saved.getMemberId())
                .isProfileUpdate(isProfileUpdate)
                .build();
    }

    public MemberResponse.UpdateProfileImg updateProfileImg(String memberId, MultipartFile imgFile) throws IOException {

        boolean isProfileImgUpdate = true;

        Member updatedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        int fileFlag = ConstUtil.UPLOAD_IMAGE_FLAG;

        // 이미지를 업로드할 디렉토리 존재 유무 확인
        fileUploadUtil.checkDirectory(fileFlag, true);

        // 변경할 이미지 정보 세팅
        Map<String, Object> profileImg = fileUploadUtil.singleFileUpload(updatedMember.getMemberIdx(), fileFlag, imgFile);
        updatedMember.updateProfileImg(profileImg);

        Member saved = memberRepository.save(updatedMember);

        return MemberResponse.UpdateProfileImg.builder()
                .memberId(saved.getMemberId())
                .isProfileImgUpdate(isProfileImgUpdate)
                .build();
    }

    public MemberResponse.DisplayProfile displayProfile(String memberId) {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        return MemberResponse.DisplayProfile.builder()
                    .memberId(memberId)
                    .memberImg(getProfileImg(member.getMemberStoreImage()))
                    .memberName(member.getMemberName())
                    .memberIntroduce(member.getMemberIntroduce())
                    .postCount(postRepository.countPostByWriterIdx(member.getMemberIdx()))
                    .followerCount(followRepository.countFollowers(member.getMemberIdx()))
                    .followingCount(followRepository.countFollowings(member.getMemberIdx()))
                .build();
    }

    public String getProfileImg(String imgName) {

        if (imgName == null) {
            return null;
        }

        String uploadPath = fileUploadUtil.getUploadPath(ConstUtil.UPLOAD_IMAGE_FLAG);
        File imageFile = new File(uploadPath, imgName);

        return fileUploadUtil.getFileEncoding(imageFile);
    }

    public MemberResponse.DisplayMainList displayMain(MemberRequest.DisplayMain request) {

        // TODO: 2024-07-08 story 정보 필요

        return MemberResponse.DisplayMainList.builder()
                .memberInfo(findMemberForMain(request))
                .postInfo(findPostsForMain(request))
                .build();
    }

    private MemberResponse.DisplayMemberMain findMemberForMain(MemberRequest.DisplayMain request) {

        Member member = memberRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        return MemberResponse.DisplayMemberMain.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberProfile(getProfileImg(member.getMemberStoreImage()))
                .build();
    }

    private MemberResponse.DisplayPostMainList findPostsForMain(MemberRequest.DisplayMain request) {

        PageRequest pageRequest = PageRequestUtil.setPageRequest(request.postScrollSize());

        List<MemberResponse.DisplayPostMain> postInfos =
                postRepositoryCustom.findPostsForMain(request.memberId(), request.lastPostIdx(), pageRequest);

        Long nextCursor = -1L;
        ScrollPaginationCollection<MemberResponse.DisplayPostMain> cursor =
                ScrollPaginationCollection.of(postInfos, request.postScrollSize());
        if (!cursor.isLastScroll()) {
            nextCursor = cursor.getNextCursor().getPostIdx();
        }

        List<MemberResponse.DisplayPostMain> currentScrollItems = cursor.getCurrentScrollItems();

        return MemberResponse.DisplayPostMainList.builder()
                .totalElements(postRepositoryCustom.countTotalPosts(request.memberId()))
                .nextCursor(nextCursor)
                .contents(appendPostImagesInfo(currentScrollItems))
                .build();
    }

    private List<MemberResponse.DisplayPostMain> appendPostImagesInfo(List<MemberResponse.DisplayPostMain> currentScrollItems) {

        currentScrollItems.forEach(postInfo ->
                postInfo.setPostImageList(postImageService.getImagesList(postInfo.getPostIdx()))
        );

        return currentScrollItems;
    }

    public MemberResponse.UpdateDeleteStatus updateDeleteStatus(MemberRequest.UpdateDeleteStatus request) {

        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));

        String deleteDate = null;
        if (request.isMemberDelete()) {
            deleteDate = timeUtil.getCurrTime(TimeFormat.TimeFormat1);
        }

        member.updateMemberDeleteDate(deleteDate);
        Member saved = memberRepository.save(member);

        return MemberResponse.UpdateDeleteStatus.builder()
                .memberId(saved.getMemberId())
                .isMemberDelete(request.isMemberDelete())
                .memberDeleteDate(saved.getMemberDeleteDate())
                .build();
    }
}