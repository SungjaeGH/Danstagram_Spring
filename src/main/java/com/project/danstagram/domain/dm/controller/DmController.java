package com.project.danstagram.domain.dm.controller;

import com.project.danstagram.domain.dm.dto.DmRequest;
import com.project.danstagram.domain.dm.dto.DmResponse;
import com.project.danstagram.domain.dm.service.DmGroupService;
import com.project.danstagram.domain.dm.service.DmLikeService;
import com.project.danstagram.domain.dm.service.DmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DmController {

    private final DmGroupService dmGroupService;
    private final DmService dmService;
    private final DmLikeService dmLikeService;

    @PostMapping("/api/{memberId}/direct/t/create")
    public ResponseEntity<DmResponse.CreateDmGroup> createDmGroup(@PathVariable String memberId, @RequestBody DmRequest.CreateDmGroup dmMembers) {

        DmResponse.CreateDmGroup dmGroup = dmGroupService.createDmGroup(memberId, dmMembers);
        if (dmGroup == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(dmGroup);
    }

    @GetMapping("/api/{memberId}/direct/t/list")
    public ResponseEntity<DmResponse.DmGroupList> findDmGroups(@PathVariable String memberId,
                                                               @RequestParam(name = "scroll-size") int scrollSize,
                                                               @RequestParam(name = "last-groupidx", required = false) Long lastDmGroupIdx) {

        DmResponse.DmGroupList dmGroups = dmGroupService.findDmGroups(memberId, scrollSize, lastDmGroupIdx);

        return ResponseEntity.ok(dmGroups);
    }

    @MessageMapping("/chat/t/{dmGroupIdx}/send")
    @SendTo("/topic/t/{dmGroupIdx}/send")
    public DmResponse.SendMessage sendDmMessage(@DestinationVariable Long dmGroupIdx,
                                                @Payload DmRequest.CreateDmMessage createDmMessageDto) {

        return dmService.createDmMessage(dmGroupIdx, createDmMessageDto);
    }

    @MessageMapping("/chat/t/{dmGroupIdx}/history")
    @SendTo("/topic/t/{dmGroupIdx}/history")
    public DmResponse.DmMsgList historyDmMessage(@DestinationVariable Long dmGroupIdx,
                                                 @Payload DmRequest.DmMessageScrollInfo scrollInfo) {

        return dmService.findDmMessages(dmGroupIdx, scrollInfo);
    }

    @MessageMapping("/chat/t/{dmGroupIdx}/{dmIdx}/delete/{memberId}")
    @SendTo("/topic/t/{dmGroupIdx}/{dmIdx}/delete/{memberId}")
    public DmResponse.DeleteDmMsg deleteDmMessage(@DestinationVariable Long dmGroupIdx,
                                                  @DestinationVariable Long dmIdx,
                                                  @DestinationVariable String memberId) {

        return dmService.changeDmMessageStatus(dmGroupIdx, dmIdx, memberId);
    }

    @MessageMapping("/chat/t/{dmGroupIdx}/{dmIdx}/like/{memberId}")
    @SendTo("/topic/t/{dmGroupIdx}/{dmIdx}/like/{memberId}")
    public DmResponse.UpdateDmLike updateDmMessageLike(@DestinationVariable Long dmGroupIdx,
                                    @DestinationVariable Long dmIdx,
                                    @DestinationVariable String memberId,
                                    @Payload DmRequest.UpdateDmLike updateDmLike) {

        updateDmLike.setDmGroupIdx(dmGroupIdx);
        updateDmLike.setDmIdx(dmIdx);
        updateDmLike.setMemberId(memberId);

        return dmLikeService.updateDmLikeStatus(updateDmLike);
    }
}
