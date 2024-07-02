package com.project.danstagram.domain.comment.controller;

import com.project.danstagram.domain.comment.dto.CommentRequest;
import com.project.danstagram.domain.comment.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/p/{postIdx}/comment/{commentIdx}/like")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PatchMapping("/update/{writerId}")
    public ResponseEntity<?> updateCommentLike(@PathVariable Map<String, String> pathVarsMap,
                                               @RequestBody CommentRequest.UpdateCommentLike updateCommentLikeDto) {

        if (!commentLikeService.updateCommentLike(pathVarsMap, updateCommentLikeDto)) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
