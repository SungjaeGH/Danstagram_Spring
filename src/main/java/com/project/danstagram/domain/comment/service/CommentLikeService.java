package com.project.danstagram.domain.comment.service;

import com.project.danstagram.domain.comment.dto.CommentRequest;
import com.project.danstagram.domain.comment.entity.Comment;
import com.project.danstagram.domain.comment.exception.CommentNotFoundException;
import com.project.danstagram.domain.comment.repository.CommentLikeRepository;
import com.project.danstagram.domain.comment.repository.CommentRepository;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
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
public class CommentLikeService {

    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean updateCommentLike(Map<String, String> pathVarsMap, CommentRequest.UpdateCommentLike updateCommentLikeDto) {

        // 게시글 존재 유무 확인
        Long postIdx = Long.parseLong(pathVarsMap.get("postIdx"));
        if (!postRepository.existsById(postIdx)) {
            throw new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx);
        }

        // 회원 존재 유무 확인
        String writerId = pathVarsMap.get("writerId");
        Member member = memberRepository.findByMemberId(writerId)
                .orElseThrow(() -> new UsernameNotFoundException("좋아요를 누른 회원을 찾을 수 없습니다. Idx: " + writerId));

        // 댓글 존재 유무 확인
        Long commentIdx = Long.parseLong(pathVarsMap.get("commentIdx"));
        Comment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다. Idx: " + commentIdx));

        // 댓글 좋아요를 한번이라도 누른 이력이 있으면, commentLike 세팅
        commentLikeRepository.findByCommentIdxAndCommentLikeMemberIdx(commentIdx, member.getMemberIdx())
                .ifPresent(commentLike -> updateCommentLikeDto.setCommentLikeIdx(commentLike.getCommentLikeIdx()));

        // 댓글 좋아요 존재 유무에 따른 update or insert
        commentLikeRepository.save(updateCommentLikeDto.toEntity(comment, member));

        return true;
    }
}
