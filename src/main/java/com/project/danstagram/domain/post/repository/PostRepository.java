package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT count(*) FROM Post p WHERE p.member.memberIdx = :writerIdx")
    Long countPostByWriter(Long writerIdx);

}
