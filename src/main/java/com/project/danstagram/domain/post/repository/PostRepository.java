package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
