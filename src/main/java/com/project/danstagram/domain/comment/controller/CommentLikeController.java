package com.project.danstagram.domain.comment.controller;

import com.project.danstagram.domain.comment.dto.CommentRequest;
import com.project.danstagram.domain.comment.dto.CommentResponse;
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
    public ResponseEntity<CommentResponse.UpdateCommentLike> updateCommentLike(@PathVariable Map<String, String> pathVarsMap,
                                                                               @RequestBody CommentRequest.UpdateCommentLike updateCommentLikeDto) {

        Long postIdx = Long.parseLong(pathVarsMap.get("postIdx"));
        String writerId = pathVarsMap.get("writerId");
        Long commentIdx = Long.parseLong(pathVarsMap.get("commentIdx"));
        updateCommentLikeDto.appendDto(postIdx, writerId, commentIdx);

        return ResponseEntity.ok(commentLikeService.updateCommentLike(updateCommentLikeDto));
    }
}
