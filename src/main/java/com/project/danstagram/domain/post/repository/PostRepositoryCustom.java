package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> findPostList(String memberId, Long lastPostIdx, Pageable pageable);

    Long countPostsByWriterId(String writerId);
}
