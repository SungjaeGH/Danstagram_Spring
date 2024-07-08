package com.project.danstagram.domain.search.service;

import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.member.service.MemberService;
import com.project.danstagram.domain.search.dto.SearchRequest;
import com.project.danstagram.domain.search.dto.SearchResponse;
import com.project.danstagram.domain.search.entity.MemberDocument;
import com.project.danstagram.domain.search.entity.Search;
import com.project.danstagram.domain.search.repository.SearchNativeQueryRepository;
import com.project.danstagram.domain.search.repository.SearchRepository;
import com.project.danstagram.domain.search.repository.SearchRepositoryCustom;
import com.project.danstagram.global.scroll.PageRequestUtil;
import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final MemberService memberService;
    private final SearchRepositoryCustom searchRepositoryCustom;
    private final SearchNativeQueryRepository searchNativeQueryRepository;
    private final MemberRepository memberRepository;
    private final SearchRepository searchRepository;
    private final TimeUtil timeUtil;

    @Transactional
    public SearchResponse.CreateSearch createSearch(SearchRequest.Create request) {

        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다. id : " + request.getMemberId()));

        Search search = request.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1));

        member.putSearch(search);
        Search saved = searchRepository.save(search);

        return SearchResponse.CreateSearch.builder()
                .searchIdx(saved.getSearchIdx())
                .searchType(saved.getSearchType())
                .searchKeyword(saved.getSearchKeyword())
                .build();
    }

    @Transactional
    public SearchResponse.RecentSearchList findRecentSearches(SearchRequest.FindRecent request) {

        PageRequest pageRequest = PageRequestUtil.setPageRequest(request.scrollSize());
        List<SearchResponse.RecentSearches> recentSearches =
                searchRepositoryCustom.findRecentSearches(request.memberId(), request.lastSearchIdx(), pageRequest);

        Long nextCursor = -1L;
        ScrollPaginationCollection<SearchResponse.RecentSearches> cursor =
                ScrollPaginationCollection.of(recentSearches, request.scrollSize());
        if (!cursor.isLastScroll()) {

            nextCursor = cursor.getNextCursor().searchIdx();
        }

        List<SearchResponse.RecentSearches> currentScrollItems = cursor.getCurrentScrollItems();

        return SearchResponse.RecentSearchList.builder()
                .totalElements(searchRepositoryCustom.countTotalRecentSearches(request.memberId()))
                .nextCursor(nextCursor)
                .recentSearches(currentScrollItems)
                .build();
    }

    @Transactional
    public SearchResponse.KeywordSearchList findKeywordSearches(SearchRequest.FindKeyword request) {

        // Member Index에서 형태소 분석 결과 조회 + 프로필 이미지 인코딩
        List<MemberDocument> members = searchNativeQueryRepository.findMemberByNativeCondition(request.keyword());
        members.forEach(memberDocument ->
                memberDocument.setMemberStoreImage(
                        memberService.getProfileImg(memberDocument.getMemberStoreImage())
                ));

        return SearchResponse.KeywordSearchList.builder()
                .totalElements((long) members.size())
                .memberInfos(members)
                .build();
    }
}