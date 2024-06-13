package com.project.danstagram.domain.follow.entity;

import com.project.danstagram.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "followIdx")
@Table(name = "follow")
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_idx", updatable = false, unique = true, nullable = false)
    private Long followIdx;

    @Column(name = "follow_role")
    private String followRole;

    @Column(name = "follow_delete_status", columnDefinition = "tinyint(1) default 0")
    private boolean followDeleteStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_to_user", referencedColumnName = "member_idx")
    private Member followToUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_from_user", referencedColumnName = "member_idx")
    private Member followFromUser;

    @Builder
    public Follow(Long followIdx, String followRole, boolean followDeleteStatus, Member followToUser, Member followFromUser) {
        this.followIdx = followIdx;
        this.followRole = followRole;
        this.followDeleteStatus = followDeleteStatus;
        this.followToUser = followToUser;
        this.followFromUser = followFromUser;
    }

    public void putFollowToUser(Member followToUser) {
        this.followToUser = followToUser;
    }

    public void putFollowFromUser(Member followFromUser) {
        this.followFromUser = followFromUser;
    }

}
