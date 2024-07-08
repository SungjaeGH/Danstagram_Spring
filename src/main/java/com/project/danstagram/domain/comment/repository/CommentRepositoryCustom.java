package com.project.danstagram.domain.comment.repository;

import com.project.danstagram.domain.comment.dto.CommentResponse;
import com.project.danstagram.domain.comment.dto.QCommentResponse_CommentInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.project.danstagram.domain.comment.entity.QComment.comment;
import static com.project.danstagram.domain.comment.entity.QCommentLike.commentLike;
import static com.project.danstagram.domain.member.entity.QMember.member;
import static com.project.danstagram.domain.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<CommentResponse.CommentInfo> findCommentsWithScroll(Long postIdx, Long lastCommentIdx, Pageable pageable) {

        return queryFactory
                .select(
                        new QCommentResponse_CommentInfo(
                                comment.commentIdx,
                                comment.commentContent,
                                comment.commentDate,
                                member.memberId,
                                member.memberStoreImage,
                                commentLike.countDistinct()
                        ))
                .from(comment)
                    .innerJoin(member).on(comment.member.memberIdx.eq(member.memberIdx))
                    .leftJoin(commentLike).on(comment.commentIdx.eq(commentLike.comment.commentIdx))
                .where(
                        ltCommentIdx(lastCommentIdx),
                        comment.post.postIdx.eq(postIdx),
                        comment.parent.commentIdx.isNull()
                )
                .groupBy(comment.commentIdx)
                .orderBy(comment.commentDate.asc())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression ltCommentIdx(Long lastCommentIdx) {

        if (lastCommentIdx == null) {
            return null;
        }

        return comment.commentIdx.gt(lastCommentIdx);
    }

    public List<CommentResponse.CommentInfo> findChildComments(Long parentCommentIdx) {

        return queryFactory
                .select(
                        new QCommentResponse_CommentInfo(
                                comment.commentIdx,
                                comment.commentContent,
                                comment.commentDate,
                                member.memberId,
                                member.memberStoreImage,
                                commentLike.countDistinct()
                        ))
                .from(comment)
                .innerJoin(member).on(comment.member.memberIdx.eq(member.memberIdx))
                .leftJoin(commentLike).on(comment.commentIdx.eq(commentLike.comment.commentIdx))
                .where(
                        comment.parent.commentIdx.eq(parentCommentIdx)
                )
                .groupBy(comment.commentIdx)
                .orderBy(comment.commentDate.asc())
                .fetch();
    }

    public Long countTotalComments(Long postIdx) {

        return queryFactory
                .select(comment.countDistinct())
                .from(comment).innerJoin(post).on(comment.post.postIdx.eq(post.postIdx))
                .where(post.postIdx.eq(postIdx))
                .fetchOne();
    }
}
