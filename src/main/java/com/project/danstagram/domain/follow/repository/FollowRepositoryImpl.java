package com.project.danstagram.domain.follow.repository;

import com.project.danstagram.domain.follow.entity.QFollow;
import com.project.danstagram.domain.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FollowRepositoryImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FollowRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<String> findFollowers(String memberId) {
        QMember member1 = new QMember("member1");
        QMember member2 = new QMember("member2");
        QFollow follow = new QFollow("follow");

        return queryFactory
                .select(member2.memberId)
                .from(member1)
                    .innerJoin(follow).on(member1.memberIdx.eq(follow.followToUser.memberIdx))
                    .innerJoin(member2).on(follow.followFromUser.memberIdx.eq(member2.memberIdx))
                .where(member1.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public List<String> findFollowings(String memberId) {
        QMember member1 = new QMember("member1");
        QMember member2 = new QMember("member2");
        QFollow follow1 = new QFollow("follow1");
        QFollow follow2 = new QFollow("follow2");

        return queryFactory
                .select(member2.memberId)
                .from(member1)
                    .innerJoin(follow1).on(member1.memberIdx.eq(follow1.followToUser.memberIdx))
                    .innerJoin(follow2).on(follow1.followToUser.memberIdx.eq(follow2.followFromUser.memberIdx)
                        .and(follow1.followFromUser.memberIdx.eq(follow2.followToUser.memberIdx)))
                    .innerJoin(member2).on(follow2.followToUser.memberIdx.eq(member2.memberIdx))
                .where(member1.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public Long countFollowers(Long memberIdx) {
        QFollow follow = new QFollow("follow");

        return queryFactory
                .select(follow.countDistinct())
                .from(follow)
                .where(follow.followToUser.memberIdx.eq(memberIdx))
                .fetchOne();
    }

    @Override
    public Long countFollowings(Long memberIdx) {
        QFollow follow1 = new QFollow("follow1");
        QFollow follow2 = new QFollow("follow2");

        return queryFactory
                .select(follow1.countDistinct())
                .from(follow1)
                    .innerJoin(follow2).on(follow1.followToUser.memberIdx.eq(follow2.followFromUser.memberIdx)
                        .and(follow1.followFromUser.memberIdx.eq(follow2.followToUser.memberIdx)))
                .where(follow1.followFromUser.memberIdx.eq(memberIdx))
                .fetchOne();
    }
}
