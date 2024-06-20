package com.project.danstagram.domain.comment.repository;

import com.project.danstagram.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT DISTINCT COUNT(c) FROM Comment c WHERE c.post.postIdx = :postIdx")
    Long countCommentsByPostIdx(Long postIdx);
}
