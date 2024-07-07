package com.project.danstagram.domain.search.repository;

import com.project.danstagram.domain.search.dto.QSearchResponse_RecentSearches;
import com.project.danstagram.domain.search.dto.SearchResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.project.danstagram.domain.member.entity.QMember.member;
import static com.project.danstagram.domain.search.entity.QSearch.search;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<SearchResponse.RecentSearches> findRecentSearches(String memberId, Long lastSearchIdx, Pageable pageable) {

        return queryFactory
                .select(
                        new QSearchResponse_RecentSearches(
                                search.searchIdx,
                                search.searchDate,
                                search.searchType,
                                search.searchKeyword
                        )
                )
                .from(search)
                .innerJoin(member).on(search.member.memberIdx.eq(member.memberIdx))
                .where(
                        ltSearchIdx(lastSearchIdx),
                        member.memberId.eq(memberId)
                )
                .orderBy(search.searchDate.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }


    private BooleanExpression ltSearchIdx(Long lastSearchIdx) {

        if (lastSearchIdx == null) {
            return null;
        }

        return search.searchIdx.lt(lastSearchIdx);
    }
}