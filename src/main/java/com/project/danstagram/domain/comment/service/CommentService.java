package com.project.danstagram.domain.comment.service;

import com.project.danstagram.domain.comment.dto.CommentResponseDto;
import com.project.danstagram.domain.comment.dto.CreateCommentDto;
import com.project.danstagram.domain.comment.entity.Comment;
import com.project.danstagram.domain.comment.exception.CommentNotFoundException;
import com.project.danstagram.domain.comment.repository.CommentRepository;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostRepository;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TimeUtil timeUtil;

    @Transactional
    public CommentResponseDto createComment(Long postIdx, String writerId, CreateCommentDto createCommentDto) {

        Comment savedComment = createCommentDto.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1));

        // 게시글 존재 유무 확인
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        // 댓글 작성자 존재 유무 확인
        Member member = memberRepository.findByMemberId(writerId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다. Id: " + writerId));

        // 부모 댓글이 존재하면 추가
        if (createCommentDto.getCommentParentIdx() != null) {
            Long parentIdx = createCommentDto.getCommentParentIdx();

            Comment parentComment = commentRepository.findById(parentIdx)
                    .orElseThrow(() -> new CommentNotFoundException("부모 댓글을 찾을 수 없습니다. Parent Idx: " + parentIdx));
            savedComment.putParent(parentComment);
        }

        post.putComment(savedComment);
        member.putComment(savedComment);

        // 댓글 저장
        return CommentResponseDto.toResponseDto(
                commentRepository.save(savedComment)
        );
    }

    @Transactional
    public boolean deleteComment(Map<String, String> commentInfoMap) {

        Long postIdx = Long.parseLong(commentInfoMap.get("postIdx"));
        Long commentIdx = Long.parseLong(commentInfoMap.get("commentIdx"));
        String writerId = commentInfoMap.get("writerId");

        // 게시물 존재 유무 체크
        if (!postRepository.existsById(postIdx)) {
            throw new PostNotFoundException("해당 게시물을 찾을 수 없습니다. Idx: " + postIdx);
        }

        // 댓글 단 회원 존재 유무 체크
        if (!memberRepository.existsByMemberId(writerId)) {
            throw new UsernameNotFoundException("해당 회원을 찾을 수 없습니다. Id: " + writerId);
        }

        // 댓글 존재 유무 체크
        Comment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new CommentNotFoundException("삭제할 댓글을 찾을 수 없습니다. idx: " + commentIdx));

        // 해당 댓글의 자식 댓글까지 모두 삭제
        deleteRecursively(comment);

        return true;
    }

    private void deleteRecursively(Comment comment) {
        List<Comment> children = comment.getChildren();
        if (children != null && !children.isEmpty()) {
            for (Comment child : children) {
                deleteRecursively(child);
            }
        }
        commentRepository.delete(comment);
    }
}
