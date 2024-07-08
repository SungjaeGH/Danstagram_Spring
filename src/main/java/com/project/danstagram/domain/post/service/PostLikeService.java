package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.dto.PostLikeRequest;
import com.project.danstagram.domain.post.dto.PostLikeResponse;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.entity.PostLike;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostLikeRepository;
import com.project.danstagram.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostLikeResponse.UpdatePostLike updatePostLike(PostLikeRequest.UpdatePostLike request) {

        // 게시글 존재 유무 확인
        Post post = postRepository.findById(request.getPostIdx())
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + request.getPostIdx()));

        // 회원 존재 유무 확인
        Member member = memberRepository.findByMemberId(request.getWriterId())
                .orElseThrow(() -> new UsernameNotFoundException("좋아요를 누른 회원을 찾을 수 없습니다. Idx: " + request.getWriterId()));

        // 게시물 좋아요를 한번이라도 누른 이력이 있으면, PostLike 세팅
        postLikeRepository.findByPostIdxAndPostLikeMemberIdx(request.getPostIdx(), member.getMemberIdx())
                .ifPresent(postLike -> request.setPostLikeIdx(postLike.getPostLikeIdx()));

        // 게시물 좋아요 존재 유무에 따른 update or insert
        PostLike saved = postLikeRepository.save(request.toEntity(post, member));

        return PostLikeResponse.UpdatePostLike.builder()
                    .postIdx(saved.getPost().getPostIdx())
                    .memberId(saved.getMember().getMemberId())
                    .isPostLike(saved.isPostLikeStatus())
                .build();
    }
}
