package com.project.danstagram.domain.post.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "postIdx")
@Table(name = "post")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_idx", updatable = false, unique = true, nullable = false)
    private Long postIdx;

    @Column(name = "post_content")
    private String postContent;

    @Column(name = "post_date")
    private String postDate;

    @Column(name = "post_update_date")
    private String postUpdateDate;

    @Column(name = "post_delete_date")
    private String postDeleteDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_idx", referencedColumnName = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    @Builder
    public Post(Long postIdx, String postContent, String postDate, String postUpdateDate, String postDeleteDate, Member member, List<PostImage> postImages) {
        this.postIdx = postIdx;
        this.postContent = postContent;
        this.postDate = postDate;
        this.postUpdateDate = postUpdateDate;
        this.postDeleteDate = postDeleteDate;
        this.member = member;
        this.postImages = postImages;
    }

    public void putMember(Member member) {
        this.member = member;
    }

    public void putPostImage(PostImage postImage) {
        this.postImages.add(postImage);
        postImage.putPost(this);
    }
}
