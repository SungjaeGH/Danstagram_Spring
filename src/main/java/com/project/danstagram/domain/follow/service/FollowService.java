package com.project.danstagram.domain.follow.service;

import com.project.danstagram.domain.follow.dto.UpdateFollowDto;
import com.project.danstagram.domain.follow.repository.FollowRepository;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean updateFollowStatus(Map<String, String> pathVarMap, UpdateFollowDto updateFollowDto) {

        String toMemberId = pathVarMap.get("toMemberId");
        String fromMemberId = pathVarMap.get("fromMemberId");

        // 팔로우 대상 회원과 누른 회원이 동일할 경우, Fail
        if (toMemberId.equals(fromMemberId)) {
            return false;
        }

        // 팔로우 대상 회원 존재 유무 확인
        Member toMember = memberRepository.findByMemberId(toMemberId)
                .orElseThrow(() -> new UsernameNotFoundException("팔로우 대상 회원을 찾을 수 없습니다. Idx: " + toMemberId));

        // 팔로우 누른 회원 존재 유무 확인
        Member fromMember = memberRepository.findByMemberId(fromMemberId)
                .orElseThrow(() -> new UsernameNotFoundException("팔로우 누른 회원을 찾을 수 없습니다. Idx: " + fromMemberId));

        followRepository.findByFollowToUserAndFollowFromUser(toMember.getMemberIdx(), fromMember.getMemberIdx())
                .ifPresent(follow -> updateFollowDto.setFollowIdx(follow.getFollowIdx()));

        followRepository.save(updateFollowDto.toEntity(toMember, fromMember));

        return true;
    }

    @Transactional
    public List<String> findFollowers(String memberId) {
        return followRepository.findFollowers(memberId);
    }

    @Transactional
    public List<String> findFollowings(String memberId) {
        return followRepository.findFollowings(memberId);
    }
}
