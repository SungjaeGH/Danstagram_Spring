package com.project.danstagram.domain.dm.repository;

import com.project.danstagram.domain.dm.dto.*;
import com.project.danstagram.domain.member.entity.QMember;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.project.danstagram.domain.dm.entity.QDm.dm;
import static com.project.danstagram.domain.dm.entity.QDmGroup.dmGroup;
import static com.project.danstagram.domain.dm.entity.QDmGroupMember.dmGroupMember;
import static com.project.danstagram.domain.dm.entity.QDmLike.dmLike;
import static com.project.danstagram.domain.member.entity.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class DmRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Long> findDmGroup(List<Long> dmMembers) {

        long dmMemberCount = dmMembers.size();

        return queryFactory
                .select(dmGroup.dmGroupIdx)
                .from(dmGroup)
                    .innerJoin(dmGroupMember).on(dmGroup.dmGroupIdx.eq(dmGroupMember.dmGroup.dmGroupIdx))
                .where(dmGroupMember.member.memberIdx.in(dmMembers))
                .groupBy(dmGroup.dmGroupIdx)
                .having(
                        dmGroupMember.member.memberIdx.countDistinct().eq(dmMemberCount)
                )
                .fetch();
    }

    public List<DmResponse.DmGroupInfo> findDmGroupList(String memberId, Long lastDmGroupIdx, Pageable pageable) {

        return queryFactory
                .select(
                        new QDmResponse_DmGroupInfo(
                                dmGroupMember.dmGroup.dmGroupIdx,
                                dmGroupMember.dmGroup.dmGroupName
                        )
                )
                .from(member)
                    .innerJoin(dmGroupMember).on(member.memberIdx.eq(dmGroupMember.member.memberIdx))
                    .innerJoin(dm).on(dm.dmGroup.dmGroupIdx.eq(dmGroupMember.dmGroup.dmGroupIdx))
                .where(
                        ltDmGroupIdx(lastDmGroupIdx),
                        member.memberId.eq(memberId)
                )
                .orderBy(dm.dmDate.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression ltDmGroupIdx(Long lastDmGroupIdx) {

        if (lastDmGroupIdx == null) {
            return null;
        }

        return dmGroupMember.dmGroup.dmGroupIdx.lt(lastDmGroupIdx);
    }

    public Map<String, DmResponse.DmGroupMemberInfo> findDmGroupMembers(Long dmGroupIdx) {

        return queryFactory
                .from(dmGroupMember, member)
                .where(dmGroupMember.dmGroup.dmGroupIdx.eq(dmGroupIdx))
                .transform(
                        groupBy(member.memberId).as(
                            new QDmResponse_DmGroupMemberInfo(
                                    member.memberId,
                                    member.memberStoreImage,
                                    member.memberStatus.eq("Login"),
                                    member.memberLogoutDate,
                                    member.memberIdx.eq(dmGroupMember.member.memberIdx)
                            )
                        )
                );
    }

    public DmResponse.LastDmMessage findLastDm(Long dmGroupIdx) {
        return queryFactory
                .select(
                        new QDmResponse_LastDmMessage(
                                dm.dmMessage,
                                dm.dmDate
                        )
                )
                .from(dm)
                .where(dm.dmGroup.dmGroupIdx.eq(dmGroupIdx))
                .orderBy(dm.dmDate.desc())
                .fetchFirst();
    }

    public List<DmResponse.DmMsgInfo> findDmMsgInfo(Long dmGroupIdx, Long lastDmIdx, Pageable pageable) {

        QMember member1 = new QMember("member1");
        QMember member2 = new QMember("member2");

        return queryFactory
                .from(dm)
                    .innerJoin(member1).on(dm.member.memberIdx.eq(member1.memberIdx))
                    .leftJoin(dmLike).on(dm.dmIdx.eq(dmLike.dm.dmIdx))
                    .leftJoin(member2).on(dmLike.member.memberIdx.eq(member2.memberIdx))
                .where(
                        ltDmIdx(lastDmIdx),
                        dm.dmGroup.dmGroupIdx.eq(dmGroupIdx),
                        dm.dmDeleteCheck.eq(false)
                )
                .orderBy(dm.dmDate.desc())
                .limit(pageable.getPageSize())
                .transform(groupBy(dm.dmIdx)
                        .list(new QDmResponse_DmMsgInfo(
                                        dm.dmIdx,
                                        member1.memberId,
                                        dm.dmMessage,
                                        dm.dmDate,
                                        GroupBy.list(member2.memberId)
                                )
                        )
                );
    }

    private BooleanExpression ltDmIdx(Long lastDmIdx) {

        if (lastDmIdx == null) {
            return null;
        }

        return dm.dmIdx.lt(lastDmIdx);
    }

    public Long countTotalDms(Long dmGroupIdx) {

        return queryFactory
                .select(dm.countDistinct())
                .from(dm)
                    .innerJoin(dmGroup).on(dm.dmGroup.dmGroupIdx.eq(dmGroup.dmGroupIdx))
                .where(dmGroup.dmGroupIdx.eq(dmGroupIdx))
                .fetchOne();
    }

    public Long countTotalDmGroups(String memberId) {

        return queryFactory
                .select(dmGroupMember.countDistinct())
                .from(dmGroupMember)
                    .innerJoin(member).on(dmGroupMember.member.memberIdx.eq(member.memberIdx))
                .where(member.memberId.eq(memberId))
                .fetchOne();
    }
}

