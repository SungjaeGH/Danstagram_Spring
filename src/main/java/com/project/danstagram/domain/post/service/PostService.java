package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.dto.CreatePostDto;
import com.project.danstagram.domain.post.dto.PostResponseDto;
import com.project.danstagram.domain.post.dto.UpdatePostDto;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.entity.PostImage;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostRepository;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostImageService postImageService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TimeUtil timeUtil;

    @Transactional
    public PostResponseDto createPost(CreatePostDto postDto, List<MultipartFile> imageFiles) throws IOException {

        Post savedPost = postDto.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1));

        // 회원 존재 유무 확인 및 Member 엔티티의 post list에 저장
        Member member = memberRepository.findByMemberId(postDto.getWriterId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다. id : " + postDto.getWriterId()));
        member.putPost(savedPost);

        Post saved = postRepository.save(savedPost);

        // Multipart로 받은 이미지 파일 정보들을 PostImage Entity List로 세팅
        List<PostImage> postImages = postImageService.setImageList(saved.getPostIdx(), imageFiles);

        // Post Entity에 이미지 파일 정보들 저장
        postImages.forEach(saved::putPostImage);
        postRepository.save(saved);

        return PostResponseDto.createPostBuilder()
                .postIdx(saved.getPostIdx())
                .createPostBuild();
    }

    @Transactional
    public PostResponseDto findPost(Long postIdx) {

        Post findPost = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        return PostResponseDto.findPostBuilder()
                .postContent(findPost.getPostContent())
                .postDate(findPost.getPostDate())
                .postUpdateDate(findPost.getPostUpdateDate())
                .postDeleteDate(findPost.getPostDeleteDate())
                .postImageList(postImageService.getImagesList(postIdx))
                .findPostBuild();
    }

    @Transactional
    public PostResponseDto updatePost(Long postIdx, UpdatePostDto updatePostDto) {

        Post updatedPost = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        postRepository.save(updatePostDto.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1), updatedPost));

        return PostResponseDto.createPostBuilder()
                .postIdx(updatedPost.getPostIdx())
                .createPostBuild();
    }
}
