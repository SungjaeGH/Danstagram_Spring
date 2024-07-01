package com.project.danstagram.domain.dm.service;

import com.project.danstagram.domain.dm.dto.DmRequest;
import com.project.danstagram.domain.dm.dto.DmResponse;
import com.project.danstagram.domain.dm.entity.Dm;
import com.project.danstagram.domain.dm.exception.DmNotFoundException;
import com.project.danstagram.domain.dm.repository.DmLikeRepository;
import com.project.danstagram.domain.dm.repository.DmRepository;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmLikeService {

    private final DmLikeRepository dmLikeRepository;
    private final MemberRepository memberRepository;
    private final DmRepository dmRepository;

    @Transactional
    public DmResponse.UpdateDmLike updateDmLikeStatus(DmRequest.UpdateDmLike updateDmLike) {

        // DM 존재 유무 확인
        Dm dm = dmRepository.findById(updateDmLike.getDmIdx())
                .orElseThrow(() -> new DmNotFoundException("해당하는 DM을 찾을 수 없습니다. dmIdx: " + updateDmLike.getDmIdx()));

        // 회원 존재 유무 확인
        Member member = memberRepository.findByMemberId(updateDmLike.getMemberId())
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다. id: " + updateDmLike.getMemberId()));

        // DM 좋아요를 한번이라도 누른 이력이 있으면, DmLike 세팅
        dmLikeRepository.findByDmIdxAndMemberIdx(updateDmLike.getDmIdx(), member.getMemberIdx())
                .ifPresent(dmLike -> updateDmLike.setDmLikeIdx(dmLike.getDmLikeIdx()));

        // DM 좋아요 존재 유무에 따른 update or insert
        dmLikeRepository.save(updateDmLike.toEntity(dm, member));

        return DmResponse.UpdateDmLike.builder()
                .dmGroupIdx(dm.getDmGroup().getDmGroupIdx())
                .dmIdx(dm.getDmIdx())
                .dmMessage(dm.getDmMessage())
                .isDmLike(updateDmLike.isDmLike())
                .build();
    }
}
