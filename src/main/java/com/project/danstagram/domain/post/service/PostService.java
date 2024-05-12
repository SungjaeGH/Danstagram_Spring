package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.dto.CreatePostDto;
import com.project.danstagram.domain.post.dto.PostResponseDto;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.entity.PostImage;
import com.project.danstagram.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostImageService postImageService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostResponseDto createPost(CreatePostDto postDto, List<MultipartFile> imageFiles) throws IOException {

        // 현재 시간 구하기
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = nowDate.format(dateFormat);

        Post savedPost = postDto.toEntity(now);

        // 회원 존재 유무 확인 및 Member 엔티티의 post list에 저장
        Member member = memberRepository.findById(postDto.getWriterIdx())
                .orElseThrow(() -> new UsernameNotFoundException("회원번호 " + postDto.getWriterIdx() + " 를 찾을 수 없습니다."));
        member.putPost(savedPost);

        // Multipart로 받은 이미지 파일 정보들을 PostImage Entity List로 세팅
        List<PostImage> postImages = postImageService.setImageList(imageFiles);

        // Post Entity에 이미지 파일 정보들 저장
        postImages.forEach(savedPost::putPostImage);

        return PostResponseDto.toResponseDto(postRepository.save(savedPost));
    }

}
