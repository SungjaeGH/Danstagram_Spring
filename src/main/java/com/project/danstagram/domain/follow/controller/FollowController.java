package com.project.danstagram.domain.follow.controller;

import com.project.danstagram.domain.follow.dto.UpdateFollowDto;
import com.project.danstagram.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{fromMemberId}/follow/")
public class FollowController {

    private final FollowService followService;

    @PatchMapping("/update/{toMemberId}")
    public ResponseEntity<?> updateFollower(@PathVariable Map<String, String> pathVarMap,
                                            @RequestBody UpdateFollowDto updateFollowDto) {

        if (!followService.updateFollowStatus(pathVarMap, updateFollowDto)) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("find/follower")
    public ResponseEntity<?> findFollower(@PathVariable String fromMemberId) {
        List<String> followers = followService.findFollowers(fromMemberId);

        if (followers.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(followers);
    }

    @GetMapping("find/following")
    public ResponseEntity<?> findFollowing(@PathVariable String fromMemberId) {
        List<String> followings = followService.findFollowings(fromMemberId);

        if (followings.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(followings);
    }
}
