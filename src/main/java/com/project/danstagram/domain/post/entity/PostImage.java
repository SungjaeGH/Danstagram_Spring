package com.project.danstagram.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "postImageIdx")
@Table(name = "post_image")
@Entity
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_idx", updatable = false, unique = true, nullable = false)
    private Long postImageIdx;

    @Column(name = "store_image_file", unique = true, nullable = false)
    private String storeImageFile;

    @Column(name = "upload_image_file", nullable = false)
    private String uploadImageFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_idx")
    private Post post;

    @Builder
    public PostImage(Long postImageIdx, String storeImageFile, String uploadImageFile, Post post) {
        this.postImageIdx = postImageIdx;
        this.storeImageFile = storeImageFile;
        this.uploadImageFile = uploadImageFile;
        this.post = post;
    }

    public void putPost(Post post) {
        this.post = post;
    }
}
