package com.project.danstagram.domain.dm.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "dmGroupIdx")
@Table(name = "dm_group")
@Entity
public class DmGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_group_idx", updatable = false, unique = true, nullable = false)
    private Long dmGroupIdx;

    @Column(name = "dm_group_name")
    private String dmGroupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_member_idx", referencedColumnName = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "dmGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DmGroupMember> dmGroupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "dmGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dm> dms = new ArrayList<>();

    @Builder
    public DmGroup(Long dmGroupIdx, String dmGroupName, Member member, List<DmGroupMember> dmGroupMembers, List<Dm> dms) {
        this.dmGroupIdx = dmGroupIdx;
        this.dmGroupName = dmGroupName;
        this.member = member;
        this.dmGroupMembers = dmGroupMembers;
        this.dms = dms;
    }

    public void putMember(Member member) {
        this.member = member;
    }

    public void putDmGroupMember(DmGroupMember dmGroupMember) {
        this.dmGroupMembers.add(dmGroupMember);
        dmGroupMember.putDmGroup(this);
    }

    public void putDm(Dm dm) {
        this.dms.add(dm);
        dm.putDmGroup(this);
    }
}
