package com.project.danstagram.domain.comment.service;

import com.project.danstagram.domain.comment.dto.CommentRequest;
import com.project.danstagram.domain.comment.dto.CommentResponse;
import com.project.danstagram.domain.comment.entity.Comment;
import com.project.danstagram.domain.comment.exception.CommentNotFoundException;
import com.project.danstagram.domain.comment.repository.CommentRepository;
import com.project.danstagram.domain.comment.repository.CommentRepositoryCustom;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.member.service.MemberService;
import com.project.danstagram.domain.post.entity.Post;
import com.project.danstagram.domain.post.exception.PostNotFoundException;
import com.project.danstagram.domain.post.repository.PostRepository;
import com.project.danstagram.global.scroll.PageRequestUtil;
import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    private final CommentRepositoryCustom commentRepositoryCustom;
    private final MemberService memberService;
    private final TimeUtil timeUtil;

    @Transactional
    public CommentResponse.CreateComment createComment(Long postIdx, String writerId, CommentRequest.CreateComment request) {

        Comment savedComment = request.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1));

        // 게시글 존재 유무 확인
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다. Idx: " + postIdx));

        // 댓글 작성자 존재 유무 확인
        Member member = memberRepository.findByMemberId(writerId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다. Id: " + writerId));

        // 부모 댓글이 존재하면 추가
        if (request.commentParentIdx() != null) {
            Long parentIdx = request.commentParentIdx();

            Comment parentComment = commentRepository.findById(parentIdx)
                    .orElseThrow(() -> new CommentNotFoundException("부모 댓글을 찾을 수 없습니다. Parent Idx: " + parentIdx));
            savedComment.putParent(parentComment);
        }

        post.putComment(savedComment);
        member.putComment(savedComment);

        // 댓글 저장
        return CommentResponse.CreateComment
                .toResponseDto(commentRepository.save(savedComment)
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

    @Transactional
    public CommentResponse.CommentList findComments(CommentRequest.FindComments request) {

        PageRequest pageRequest = PageRequestUtil.setPageRequest(request.scrollSize());
        List<CommentResponse.CommentInfo> commentInfo =
                commentRepositoryCustom.findCommentsWithScroll(request.postIdx(), request.lastCommentIdx(), pageRequest);

        Long nextCursor = -1L;
        ScrollPaginationCollection<CommentResponse.CommentInfo> cursor = ScrollPaginationCollection.of(commentInfo, request.scrollSize());
        if (!cursor.isLastScroll()) {
            nextCursor = cursor.getNextCursor().getCommentIdx();
        }

        List<CommentResponse.CommentInfo> currentScrollItems = cursor.getCurrentScrollItems();

        return CommentResponse.CommentList.builder()
                .totalElements(commentRepositoryCustom.countTotalComments(request.postIdx()))
                .nextCursor(nextCursor)
                .commentInfos(appendChildComments(currentScrollItems))
                .build();
    }

    private List<CommentResponse.CommentInfo> appendChildComments(List<CommentResponse.CommentInfo> currentScrollItems) {

        for (CommentResponse.CommentInfo commentInfo : currentScrollItems) {
            // 자식 댓글 조회
            List<CommentResponse.CommentInfo> childComments = commentRepositoryCustom.findChildComments(commentInfo.getCommentIdx());
            if (!childComments.isEmpty()) {
                // 자식 댓글 존재할 경우, 해당 댓글의 자식 댓글 조회
                List<CommentResponse.CommentInfo> addList = appendChildComments(childComments);
                commentInfo.setChildCommentInfos(addList);
            }

            // 이미지 파일 인코딩
            commentInfo.setWriterImg(memberService.getProfileImg(commentInfo.getWriterImg()));
        }

        return currentScrollItems;
    }
}
