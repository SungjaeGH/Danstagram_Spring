package com.project.danstagram.domain.dm.service;

import com.project.danstagram.domain.dm.dto.DmRequest;
import com.project.danstagram.domain.dm.dto.DmResponse;
import com.project.danstagram.domain.dm.entity.Dm;
import com.project.danstagram.domain.dm.entity.DmGroup;
import com.project.danstagram.domain.dm.exception.DmNotFoundException;
import com.project.danstagram.domain.dm.repository.DmGroupRepository;
import com.project.danstagram.domain.dm.repository.DmRepository;
import com.project.danstagram.domain.dm.repository.DmRepositoryCustom;
import com.project.danstagram.domain.member.entity.Member;
import com.project.danstagram.domain.member.repository.MemberRepository;
import com.project.danstagram.global.scroll.PageRequestUtil;
import com.project.danstagram.global.scroll.ScrollPaginationCollection;
import com.project.danstagram.global.time.TimeFormat;
import com.project.danstagram.global.time.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DmService {

    private final DmGroupRepository dmGroupRepository;
    private final MemberRepository memberRepository;
    private final DmRepository dmRepository;
    private final DmRepositoryCustom dmRepositoryCustom;
    private final TimeUtil timeUtil;

    @Transactional
    public DmResponse.SendMessage createDmMessage(Long dmGroupIdx, DmRequest.CreateDmMessage createDmMessageDto) {

        Dm createdDm = createDmMessageDto.toEntity(timeUtil.getCurrTime(TimeFormat.TimeFormat1));

        // 해당하는 Dm Group이 존재하는지 확인
        DmGroup dmGroup = dmGroupRepository.findById(dmGroupIdx)
                .orElseThrow(() -> new DmNotFoundException("해당하는 Dm Group이 존재하지 않습니다. idx : " + dmGroupIdx));

        // Dm Message를 작성한 회원이 존재하는지 확인
        String writerId = createDmMessageDto.writerId();
        Member member = memberRepository.findByMemberId(writerId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다. id : " + writerId));


        // DM 메시지 정보 저장
        dmGroup.putDm(createdDm);
        member.putDm(createdDm);
        Dm savedDm = dmRepository.save(createdDm);

        return DmResponse.SendMessage.builder()
                .dmIdx(savedDm.getDmIdx())
                .writerId(writerId)
                .dmMessage(createDmMessageDto.dmMessage())
                .build();
    }

    @Transactional
    public DmResponse.DmMsgList findDmMessages(Long dmGroupIdx, DmRequest.DmMessageScrollInfo scrollInfo) {

        PageRequest pageRequest = PageRequestUtil.setPageRequest(scrollInfo.getScrollSize());
        List<DmResponse.DmMsgInfo> dmMsgInfo = dmRepositoryCustom.findDmMsgInfo(dmGroupIdx, scrollInfo.getLastDmIdx(), pageRequest);

        Long nextCursor = -1L;
        ScrollPaginationCollection<DmResponse.DmMsgInfo> cursor = ScrollPaginationCollection.of(dmMsgInfo, scrollInfo.getScrollSize());
        if (!cursor.isLastScroll()) {
            nextCursor = cursor.getNextCursor().getDmIdx();
        }

        List<DmResponse.DmMsgInfo> currentScrollItems = cursor.getCurrentScrollItems();

        return DmResponse.DmMsgList.builder()
                .totalElements(scrollInfo.getScrollSize())
                .nextCursor(nextCursor)
                .dmMsgs(currentScrollItems)
                .build();
    }

    @Transactional
    public DmResponse.DeleteDmMsg changeDmMessageStatus(Long dmGroupIdx, Long dmIdx, String memberId) {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다. id: " + memberId));

        Dm patchedDm = dmRepository.findById(dmIdx)
                .orElseThrow(() -> new DmNotFoundException("해당 메시지 그룹 내 메시지가 존재하지 않습니다. " +
                        "groupIdx: " + dmGroupIdx + ", dmIdx: " + dmIdx));

        // 해당 메시지의 작성자와 메시지 삭제를 요청한 요청인이 다를 경우 삭제 불가
        if (!Objects.equals(member.getMemberIdx(), patchedDm.getMember().getMemberIdx())) {
            throw new IllegalStateException("해당 메시지의 작성자만 메시지를 삭제할 수 있습니다. " +
                    "작성자: " + patchedDm.getMember().getMemberId() + ", 요청인: " + memberId);
        }

        patchedDm.changeDmDeleteStatus(true);

        return DmResponse.DeleteDmMsg.toResponse(dmRepository.save(patchedDm));
    }

}
