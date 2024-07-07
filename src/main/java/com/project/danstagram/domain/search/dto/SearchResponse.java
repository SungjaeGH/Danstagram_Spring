package com.project.danstagram.domain.search.dto;

import com.project.danstagram.domain.search.entity.MemberDocument;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.util.List;

public class SearchResponse {

    /* createSearch */
    @Builder
    public record CreateSearch(Long searchIdx, String searchType, String searchKeyword) {

    }

    /* findRecentSearches */
    @Builder
    public record RecentSearchList(int totalElements, Long nextCursor, List<RecentSearches> recentSearches) {

    }

    public record RecentSearches(Long searchIdx, String searchDate, String searchType, String searchKeyword) {

        @QueryProjection
        public RecentSearches {
        }
    }

    /* findKeywordSearches */
    @Builder
    public record KeywordSearchList(Long totalElements, List<MemberDocument> memberInfos) {
    }
}
