package com.project.danstagram.domain.comment.repository;

import com.project.danstagram.domain.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT cl FROM CommentLike cl WHERE cl.comment.commentIdx = :commentIdx and cl.member.memberIdx = :commentLikeMemberIdx")

    Optional<CommentLike> findByCommentIdxAndCommentLikeMemberIdx(Long commentIdx, Long commentLikeMemberIdx);

}
