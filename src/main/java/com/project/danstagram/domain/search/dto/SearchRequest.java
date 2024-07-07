package com.project.danstagram.domain.search.dto;

import com.project.danstagram.domain.search.entity.Search;
import com.project.danstagram.domain.search.entity.SearchType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class SearchRequest {

    @Getter
    @RequiredArgsConstructor
    public static class Create {

        @NotNull(message = "HashTag, Account, Place Type만 유효합니다.")
        private final SearchType searchType;
        private final String searchKeyword;
        private String memberId;

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public Search toEntity(String currDate) {
            return Search.builder()
                    .searchDate(currDate)
                    .searchType(this.searchType.toString())
                    .searchKeyword(this.searchKeyword)
                    .build();
        }
    }

    public record FindRecent(String memberId, int scrollSize, Long lastSearchIdx) {
    }

    public record FindKeyword(String keyword) {
    }

}
