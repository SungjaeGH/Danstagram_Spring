package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Query("SELECT p FROM PostImage p WHERE p.post.postIdx = :postIdx")
    List<PostImage> findByPostIdx(Long postIdx);
}
