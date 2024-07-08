package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.member.dto.MemberResponse;
import com.project.danstagram.domain.member.dto.QMemberResponse_DisplayPostMain;
import com.project.danstagram.domain.post.dto.PostResponse;
import com.project.danstagram.domain.post.dto.QPostResponse_PostInfoForProfile;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.project.danstagram.domain.member.entity.QMember.member;
import static com.project.danstagram.domain.post.entity.QPost.post;
import static com.project.danstagram.domain.post.entity.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<PostResponse.PostInfoForProfile> findPostForProfile(String memberId, Long lastPostIdx, Pageable pageable) {

        return queryFactory
                .select(
                        new QPostResponse_PostInfoForProfile(
                                post.postIdx,
                                postLike.countDistinct()
                        )
                )
                .from(post)
                    .innerJoin(member).on(post.member.memberIdx.eq(member.memberIdx))
                    .leftJoin(postLike).on(post.postIdx.eq(postLike.post.postIdx))
                .where(
                        ltPostIdx(lastPostIdx),
                        member.memberId.eq(memberId)
                )
                .groupBy(post.postIdx)
                .orderBy(post.postIdx.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression ltPostIdx(Long lastPostIdx) {

        if (lastPostIdx == null) {
            return null;
        }

        return post.postIdx.lt(lastPostIdx);
    }

    public Long countTotalPosts(String writerId) {

        return queryFactory
                .select(post.countDistinct())
                .from(post).innerJoin(member)
                    .on(post.member.memberIdx.eq(member.memberIdx))
                .where(post.member.memberId.eq(writerId))
                .fetchOne();
    }

    public List<MemberResponse.DisplayPostMain> findPostsForMain(String memberId, Long lastPostIdx, Pageable pageable) {

        return queryFactory
                .select(new QMemberResponse_DisplayPostMain(
                        post.postIdx,
                        member.memberId,
                        post.postContent,
                        post.postDate,
                        post.postUpdateDate,
                        postLike.countDistinct()
                ))
                .from(post)
                    .innerJoin(member).on(post.member.memberIdx.eq(member.memberIdx))
                    .leftJoin(postLike).on(post.postIdx.eq(postLike.post.postIdx))
                .where(
                        ltPostIdx(lastPostIdx),
                        member.memberId.eq(memberId)
                )
                .groupBy(post.postIdx)
                .orderBy(post.postIdx.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
