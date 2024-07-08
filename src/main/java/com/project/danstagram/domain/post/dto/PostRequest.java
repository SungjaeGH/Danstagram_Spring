package com.project.danstagram.domain.post.dto;

import com.project.danstagram.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

public class PostRequest {

    public record CreatePost(String writerId, String postContent) {

        public Post toEntity(String currDate) {
            return Post.builder()
                    .postContent(postContent)
                    .postDate(currDate)
                    .postImages(new ArrayList<>())
                    .build();
        }
    }

    public record UpdatePost(String updateContent) {

        public Post toEntity(String updateDate, Post targetPost) {
            return Post.builder()
                    .postIdx(targetPost.getPostIdx())
                    .postContent(this.updateContent)
                    .postDate(targetPost.getPostDate())
                    .postUpdateDate(updateDate)
                    .postDeleteDate(targetPost.getPostDeleteDate())
                    .member(targetPost.getMember())
                    .postImages(targetPost.getPostImages())
                    .comment(targetPost.getComment())
                    .build();
        }
    }

    public record FindPostForProfile(String memberId, int scrollSize, Long lastPostIdx) {
    }

    @Getter
    @AllArgsConstructor
    public static class UpdatePostDeleteStatus {

        private Long postIdx;
        private final boolean isPostDelete;

        public void setPostIdx(Long postIdx) {
            this.postIdx = postIdx;
        }

        public Post toEntity(Post updated, String deleteDate) {
            return Post.builder()
                        .postIdx(updated.getPostIdx())
                        .postContent(updated.getPostContent())
                        .postDate(updated.getPostDate())
                        .postUpdateDate(updated.getPostDate())
                        .postDeleteDate(deleteDate)
                        .member(updated.getMember())
                        .postImages(updated.getPostImages())
                        .comment(updated.getComment())
                    .build();
        }
    }
}