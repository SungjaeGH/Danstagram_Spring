package com.project.danstagram.domain.post.controller;

import com.project.danstagram.domain.post.dto.PostLikeRequest;
import com.project.danstagram.domain.post.dto.PostLikeResponse;
import com.project.danstagram.domain.post.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/p/{postIdx}/like")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PatchMapping("/update/{writerId}")
    public ResponseEntity<PostLikeResponse.UpdatePostLike> updatePostLike(@PathVariable Map<String, String> pathVarsMap,
                                                                          @RequestBody PostLikeRequest.UpdatePostLike updatePostLikeDto) {

        PostLikeRequest.UpdatePostLike updatePostLike =
                updatePostLikeDto.appendDto(Long.parseLong(pathVarsMap.get("postIdx")), pathVarsMap.get("writerId"));

        return ResponseEntity.ok(postLikeService.updatePostLike(updatePostLike));
    }
}
