package com.project.danstagram.global.scroll;

import org.springframework.data.domain.PageRequest;

public class PageRequestUtil {

    public static PageRequest setPageRequest(int pageSize) {
        // 다음 스크롤의 요소를 확인하기 위해 조회할 데이터 수 + 1
        return PageRequest.of(0, pageSize + 1);
    }
}
