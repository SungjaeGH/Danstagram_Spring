package com.project.danstagram.domain.post.controller;

import com.project.danstagram.domain.post.dto.UpdatePostLikeDto;
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
    public ResponseEntity<?> updatePostLike(@PathVariable Map<String, String> pathVarsMap,
                                            @RequestBody UpdatePostLikeDto updatePostLikeDto) {

        if (!postLikeService.updatePostLike(pathVarsMap, updatePostLikeDto)) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
