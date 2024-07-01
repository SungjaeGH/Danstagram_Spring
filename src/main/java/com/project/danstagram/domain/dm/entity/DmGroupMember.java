package com.project.danstagram.domain.dm.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "dmGroupMemberIdx")
@Table(name = "dm_group_member")
@Entity
public class DmGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_group_member_idx", updatable = false, unique = true, nullable = false)
    private Long dmGroupMemberIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dm_group_idx", referencedColumnName = "dm_group_idx")
    private DmGroup dmGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", referencedColumnName = "member_idx")
    private Member member;

    @Builder
    public DmGroupMember(Long dmGroupMemberIdx, DmGroup dmGroup, Member member) {
        this.dmGroupMemberIdx = dmGroupMemberIdx;
        this.dmGroup = dmGroup;
        this.member = member;
    }

    public void putDmGroup(DmGroup dmGroup) {
        this.dmGroup = dmGroup;
    }

    public void putMember(Member member) {
        this.member = member;
    }
}
