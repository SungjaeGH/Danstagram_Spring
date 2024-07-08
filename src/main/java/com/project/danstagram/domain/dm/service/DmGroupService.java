package com.project.danstagram.domain.dm.service;

import com.project.danstagram.domain.dm.dto.*;
import com.project.danstagram.domain.dm.entity.DmGroup;
import com.project.danstagram.domain.dm.entity.DmGroupMember;
import com.project.danstagram.domain.dm.repository.DmGroupRepository;
import com.project.danstagram.domain.dm.repository.DmRepositoryCustom;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.domain.member.service.MemberService;
import com.project.danstagram.global.scroll.PageRequestUtil;
import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmGroupService {

    private final MemberRepository memberRepository;
    private final DmGroupRepository dmGroupRepository;
    private final DmRepositoryCustom dmRepositoryCustom;
    private final MemberService memberService;

    @Transactional
    public DmResponse.CreateDmGroup createDmGroup(String leaderId, DmRequest.CreateDmGroup dmMembers) {

        List<String> dmMembersName = dmMembers.getTargetMembers();
        dmMembersName.add(leaderId);

        // 채팅방 그룹 회원들이 존재하는지 체크
        List<Member> members = memberRepository.findByMemberIdIn(dmMembersName);
        if (!checkAllMemberValid(dmMembersName, members)) {
            return null;
        }

        // 방장 회원 정보 가져오기
        Member leaderMember = members.stream()
                .filter(member -> member.getMemberId().equals(leaderId))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("방장 회원 정보를 찾을 수 없습니다. id : " + leaderId));

        // Member List -> MemberIdx List 재구성
        List<Long> dmMemberIdxs = members.stream()
                .map(Member::getMemberIdx).collect(Collectors.toList());

        // DmGroup 엔티티에 해당하는 Group이 존재하는지 확인
        List<Long> dmGroup = dmRepositoryCustom.findDmGroup(dmMemberIdxs);
        if (!dmGroup.isEmpty()) {
            // 존재할 경우 해당 dmGroupIdx 반환
            return DmResponse.CreateDmGroup.builder()
                    .dmGroupIdx(leaderMember.getMemberIdx())
                    .build();
        }

        // 존재하지 않을 경우, DmGroup + DmGroupMember 엔티티에 추가
        List<DmGroupMember> dmGroupMembers = members.stream()
                .map(DmRequest.CreateDmGroupMember::toEntity)
                .toList();

        // DMGroup 정보 및 Member 정보 저장
        DmGroup savedGroup = DmRequest.CreateDmGroup.toEntity(leaderMember, makeDmGroupName(dmMembersName));
        dmGroupMembers.forEach(savedGroup::putDmGroupMember);
        DmGroup saved = dmGroupRepository.save(savedGroup);

        return DmResponse.CreateDmGroup.builder()
                .dmGroupIdx(saved.getDmGroupIdx())
                .build();
    }

    private boolean checkAllMemberValid(List<String> dmMembers, List<Member> members) {

        Map<String, Long> memberMap = members.stream()
                .collect(Collectors.toMap(Member::getMemberId, Member::getMemberIdx));

        for (String memberId : dmMembers) {
            Long memberIdx = memberMap.get(memberId);

            if (memberIdx == null) {
                throw new UsernameNotFoundException("해당하는 소셜 회원을 찾을 수 없습니다: " + memberId);
            }
        }

        return true;
    }

    private String makeDmGroupName(List<String> membersName) {

        StringBuilder sb = new StringBuilder();

        for (int nameIdx = 0; nameIdx < membersName.size(); nameIdx++) {

            sb.append(membersName.get(nameIdx));
            if (nameIdx != membersName.size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    @Transactional
    public DmResponse.DmGroupList findDmGroups(String memberId, int scrollSize, Long lastDmGroupIdx) {

        PageRequest pageRequest = PageRequestUtil.setPageRequest(scrollSize);
        List<DmResponse.DmGroupInfo> dmGroupList = dmRepositoryCustom.findDmGroupList(memberId, lastDmGroupIdx, pageRequest);

        Long nextCursor = -1L;
        ScrollPaginationCollection<DmResponse.DmGroupInfo> dmGroupCursor = ScrollPaginationCollection.of(dmGroupList, scrollSize);
        if (!dmGroupCursor.isLastScroll()) {
            nextCursor = dmGroupCursor.getNextCursor().getDmGroupIdx();
        }

        List<DmResponse.DmGroupInfo> currentScrollItems = dmGroupCursor.getCurrentScrollItems();

        return DmResponse.DmGroupList.builder()
                .totalElements(dmRepositoryCustom.countTotalDmGroups(memberId))
                .nextCursor(nextCursor)
                .dmGroupList(AppendInfoInDmGroupInfos(currentScrollItems))
                .build();
    }

    private List<DmResponse.DmGroupInfo> AppendInfoInDmGroupInfos(List<DmResponse.DmGroupInfo> currentScrollItems) {

        for (DmResponse.DmGroupInfo dmGroupInfo : currentScrollItems) {

            Long dmGroupIdx = dmGroupInfo.getDmGroupIdx();

            // 최근에 온 DM 검색
            dmGroupInfo.setLastDmMsg(dmRepositoryCustom.findLastDm(dmGroupIdx));

            // 채팅방 맴버 정보 검색
            Map<String, DmResponse.DmGroupMemberInfo> dmGroupMembers = dmRepositoryCustom.findDmGroupMembers(dmGroupIdx);
            dmGroupMembers.forEach((member, dmGroupMember) ->
                    // 이미지 파일 인코딩
                    dmGroupMember.setProfileImg(memberService.getProfileImg(dmGroupMember.getProfileImg()))
            );
            dmGroupInfo.setDmMembers(dmGroupMembers);
        }

        return currentScrollItems;
    }
}
