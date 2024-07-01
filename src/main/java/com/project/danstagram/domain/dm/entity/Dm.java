package com.project.danstagram.domain.dm.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "dmIdx")
@Table(name = "dm")
@Entity
public class Dm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_idx", updatable = false, unique = true, nullable = false)
    private Long dmIdx;

    @Column(name = "dm_message")
    private String dmMessage;

    @Column(name = "dm_date")
    private String dmDate;

    @Column(name = "dm_delete_check", columnDefinition = "tinyint(1) default 0")
    private boolean dmDeleteCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dm_group_idx", referencedColumnName = "dm_group_idx")
    private DmGroup dmGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", referencedColumnName = "member_idx")
    private Member member;

    @OneToMany(mappedBy = "dm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DmLike> dmLikes = new ArrayList<>();

    @Builder
    public Dm(Long dmIdx, String dmMessage, String dmDate, boolean dmDeleteCheck, DmGroup dmGroup, Member member, List<DmLike> dmLikes) {
        this.dmIdx = dmIdx;
        this.dmMessage = dmMessage;
        this.dmDate = dmDate;
        this.dmDeleteCheck = dmDeleteCheck;
        this.dmGroup = dmGroup;
        this.member = member;
        this.dmLikes = dmLikes;
    }

    public void putDmGroup(DmGroup dmGroup) {
        this.dmGroup = dmGroup;
    }

    public void putMember(Member member) {
        this.member = member;
    }

    public void putDmLike(DmLike dmLike) {
        this.dmLikes.add(dmLike);
        dmLike.putDm(this);
    }

    public void changeDmDeleteStatus(boolean status) {
        this.dmDeleteCheck = status;
    }
}
