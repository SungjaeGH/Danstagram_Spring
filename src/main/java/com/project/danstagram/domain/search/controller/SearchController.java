package com.project.danstagram.domain.search.controller;

import com.project.danstagram.domain.search.dto.SearchRequest;
import com.project.danstagram.domain.search.dto.SearchResponse;
import com.project.danstagram.domain.search.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{memberId}/search")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/create")
    @Validated
    public ResponseEntity<SearchResponse.CreateSearch> createSearch(@PathVariable String memberId,
                                                                    @Valid @RequestBody SearchRequest.Create createSearchDto) {

        createSearchDto.setMemberId(memberId);

        return ResponseEntity.ok(searchService.createSearch(createSearchDto));
    }


    @GetMapping("/recent")
    public ResponseEntity<SearchResponse.RecentSearchList> findRecentSearches(@PathVariable String memberId,
                                                                              @RequestParam(name = "scroll-size") int scrollSize,
                                                                              @RequestParam(name = "last-searchidx", required = false) Long lastSearchIdx) {

        SearchRequest.FindRecent request = new SearchRequest.FindRecent(memberId, scrollSize, lastSearchIdx);

        return ResponseEntity.ok(searchService.findRecentSearches(request));
    }

    @GetMapping("/find")
    public ResponseEntity<SearchResponse.KeywordSearchList> findKeywordSearches(@PathVariable String memberId,
                                                                                @RequestParam(name = "keyword") String keyword) {

        SearchRequest.FindKeyword request = new SearchRequest.FindKeyword(keyword);

        return ResponseEntity.ok(searchService.findKeywordSearches(request));
    }
}