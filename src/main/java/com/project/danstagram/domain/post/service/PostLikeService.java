package com.project.danstagram.domain.post.service;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.dto.UpdatePostLikeDto;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostLikeRepository;
import com.project.danstagram.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public boolean updatePostLike(Map<String, String> pathVarsMap, UpdatePostLikeDto updatePostLikeDto) {

        // 게시글 존재 유무 확인
        Long postIdx = Long.parseLong(pathVarsMap.get("postIdx"));
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        // 회원 존재 유무 확인
        String writerId = pathVarsMap.get("writerId");
        Member member = memberRepository.findByMemberId(writerId)
                .orElseThrow(() -> new UsernameNotFoundException("좋아요를 누른 회원을 찾을 수 없습니다. Idx: " + writerId));

        // 게시물 좋아요를 한번이라도 누른 이력이 있으면, PostLike 세팅
        postLikeRepository.findByPostIdxAndPostLikeMemberIdx(postIdx, member.getMemberIdx())
                .ifPresent(postLike -> updatePostLikeDto.setPostLikeIdx(postLike.getPostLikeIdx()));

        // 게시물 좋아요 존재 유무에 따른 update or insert
        postLikeRepository.save(updatePostLikeDto.toEntity(post, member));

        return true;
    }
}
