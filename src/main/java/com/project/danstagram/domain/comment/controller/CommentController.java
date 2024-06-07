package com.project.danstagram.domain.comment.controller;

import com.project.danstagram.domain.comment.dto.CommentResponseDto;
import com.project.danstagram.domain.comment.dto.CreateCommentDto;
import com.project.danstagram.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/p/{postIdx}/comment/")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create/{writerId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postIdx,
                                                         @PathVariable String writerId,
                                                         @RequestBody CreateCommentDto createCommentDto) {

        CommentResponseDto createResult = commentService.createComment(postIdx, writerId, createCommentDto);
        if (createResult.getCommentIdx() == 0) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(createResult);
    }

    @DeleteMapping("{commentIdx}/delete/{writerId}")
    public ResponseEntity<CommentResponseDto> deleteComment(@PathVariable Map<String, String> pathVarsMap) {

        if (!commentService.deleteComment(pathVarsMap)) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
