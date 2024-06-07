package com.project.danstagram.domain.comment.entity;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "commentIdx")
@Table(name = "comment")
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx", updatable = false, unique = true, nullable = false)
    private Long commentIdx;

    @Column(name = "comment_content")
    private String commentContent;

    @Column(name = "comment_date")
    private String commentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_post_idx", referencedColumnName = "post_idx")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_idx", referencedColumnName = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "comment_parent_idx")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(Long commentIdx, String commentContent, String commentDate, Post post, Member member, List<CommentLike> commentLikes, Comment parent, List<Comment> children) {
        this.commentIdx = commentIdx;
        this.commentContent = commentContent;
        this.commentDate = commentDate;
        this.post = post;
        this.member = member;
        this.commentLikes = commentLikes;
        this.parent = parent;
        this.children = children;
    }

    public void putPost(Post post) {
        this.post = post;
    }

    public void putMember(Member member) {
        this.member = member;
    }

    public void putCommentLike(CommentLike commentLike) {
        this.commentLikes.add(commentLike);
        commentLike.putComment(this);
    }

    public void putParent(Comment parent) {
        this.parent = parent;
    }
}