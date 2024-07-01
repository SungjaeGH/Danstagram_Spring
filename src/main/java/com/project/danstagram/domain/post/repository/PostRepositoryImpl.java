package com.project.danstagram.domain.post.repository;

import com.project.danstagram.domain.post.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.project.danstagram.domain.member.entity.QMember.member;
import static com.project.danstagram.domain.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPostList(String memberId, Long lastPostIdx, Pageable pageable) {

        return queryFactory
                .select(post)
                .from(post)
                    .innerJoin(member).on(post.member.memberIdx.eq(member.memberIdx))
                .where(
                        ltPostIdx(lastPostIdx),
                        member.memberId.eq(memberId)
                )
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

    @Override
    public Long countPostsByWriterId(String writerId) {

        return queryFactory
                .select(post.countDistinct())
                .from(post).innerJoin(member)
                    .on(post.member.memberIdx.eq(member.memberIdx))
                .where(post.member.memberId.eq(writerId))
                .fetchOne();
    }
}
