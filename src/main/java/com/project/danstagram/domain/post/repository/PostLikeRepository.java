package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    @Query("SELECT pl FROM PostLike pl WHERE pl.post.postIdx = :postIdx and pl.member.memberIdx = :postLikeMemberIdx")
    Optional<PostLike> findByPostIdxAndPostLikeMemberIdx(Long postIdx, Long postLikeMemberIdx);

    @Query("SELECT DISTINCT COUNT(pl) FROM PostLike pl WHERE pl.post.postIdx = :postIdx")
    Long countPostLikes(Long postIdx);
}
