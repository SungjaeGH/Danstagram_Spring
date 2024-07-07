package com.project.danstagram.domain.search.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "searchIdx")
@Table(name = "search")
@Entity
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_idx", updatable = false, unique = true, nullable = false)
    private Long searchIdx;

    @Column(name = "search_date")
    private String searchDate;

    @Column(name = "search_type")
    private String searchType;

    @Column(name = "search_keyword")
    private String searchKeyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", referencedColumnName = "member_idx")
    private Member member;

    @Builder
    public Search(Long searchIdx, String searchDate, String searchType, String searchKeyword, Member member) {
        this.searchIdx = searchIdx;
        this.searchDate = searchDate;
        this.searchType = searchType;
        this.searchKeyword = searchKeyword;
        this.member = member;
    }

    public void putMember(Member member) {
        this.member = member;
    }
}
