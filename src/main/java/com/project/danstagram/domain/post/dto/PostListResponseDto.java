package com.project.danstagram.domain.post.dto;

import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostListResponseDto {

    private static final Long LAST_CURSOR = -1L;

    private Long totalElements;
    private Long nextCursor;
    private List<PostInfoResponseDto> contents = new ArrayList<>();

    public PostListResponseDto(Long totalElements, Long nextCursor, List<PostInfoResponseDto> contents) {
        this.totalElements = totalElements;
        this.nextCursor = nextCursor;
        this.contents = contents;
    }

    public static PostListResponseDto of(ScrollPaginationCollection<PostInfoResponseDto> postScroll, Long totalElements) {

        if (postScroll.isLastScroll()) {
            // 다음 스크롤이 존재하지 않을 경우 -1를 담아 객체 생성
            return PostListResponseDto.newScrollHasNext(totalElements, LAST_CURSOR, postScroll.getCurrentScrollItems());
        }

        Long nextCursor = postScroll.getNextCursor().getPostIdx();
        return PostListResponseDto.newScrollHasNext(totalElements, nextCursor, postScroll.getCurrentScrollItems());
    }

    private static PostListResponseDto newScrollHasNext(Long totalElements, Long nextCursor, List<PostInfoResponseDto> postScroll) {

        return PostListResponseDto.builder()
                    .totalElements(totalElements)
                    .nextCursor(nextCursor)
                    .contents(postScroll)
                .build();
    }
}
