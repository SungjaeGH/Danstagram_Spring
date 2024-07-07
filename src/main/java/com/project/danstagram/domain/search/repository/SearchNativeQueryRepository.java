package com.project.danstagram.domain.search.repository;

import com.project.danstagram.domain.search.entity.MemberDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SearchNativeQueryRepository {

    private final ElasticsearchOperations operations;

    public List<MemberDocument> findMemberByNativeCondition(String keyword) {

        NativeQuery query = createConditionNativeQuery(keyword);

        SearchHits<MemberDocument> searched = operations.search(query, MemberDocument.class);

        return searched.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    private NativeQuery createConditionNativeQuery(String keyword) {

        List<String> filedList = setFieldNames();

        return NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(mul -> mul
                                .query(keyword)
                                .fields(filedList)
                        ))
                .build();
    }

    private List<String> setFieldNames() {

        String[] fields = {"memberId", "memberName"};
        String[] analyzerList = {"keyword", "jaso", "nori"};

        return Arrays.stream(fields)
                .flatMap(f -> Arrays.stream(analyzerList)
                        .map(a -> f + "." + a))
                .collect(Collectors.toList());
    }
}
