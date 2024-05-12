package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

}
