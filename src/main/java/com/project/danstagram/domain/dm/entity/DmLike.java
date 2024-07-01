package com.project.danstagram.domain.dm.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "dmLikeIdx")
@Table(name = "dm_like")
@Entity
public class DmLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_like_idx", updatable = false, unique = true, nullable = false)
    private Long dmLikeIdx;

    @Column(name = "dm_like_statue", columnDefinition = "tinyint(1) default 0")
    private boolean dmLikeStatue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dm_idx", referencedColumnName = "dm_idx")
    private Dm dm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", referencedColumnName = "member_idx")
    private Member member;

    @Builder
    public DmLike(Long dmLikeIdx, boolean dmLikeStatue, Dm dm, Member member) {
        this.dmLikeIdx = dmLikeIdx;
        this.dmLikeStatue = dmLikeStatue;
        this.dm = dm;
        this.member = member;
    }

    public void putDm(Dm dm) {
        this.dm = dm;
    }

    public void putMember(Member member) {
        this.member = member;
    }
}
