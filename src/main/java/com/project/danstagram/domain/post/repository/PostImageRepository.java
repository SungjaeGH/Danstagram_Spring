package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Query("SELECT pi FROM PostImage pi WHERE pi.post.postIdx = :postIdx")
    List<PostImage> findByPostIdx(Long postIdx);

    @Query("SELECT pi FROM PostImage pi WHERE pi.post.postIdx = :postIdx ORDER BY pi.postImageIdx limit 1")
    Optional<PostImage> findTopImg(Long postIdx);
}
