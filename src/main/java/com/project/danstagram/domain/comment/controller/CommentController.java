package com.project.danstagram.domain.comment.controller;

import com.project.danstagram.domain.comment.dto.CommentRequest;
import com.project.danstagram.domain.comment.dto.CommentResponse;
import com.project.danstagram.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/p/{postIdx}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create/{writerId}")
    public ResponseEntity<CommentResponse.CreateComment> createComment(@PathVariable Long postIdx,
                                                                       @PathVariable String writerId,
                                                                       @RequestBody CommentRequest.CreateComment createCommentDto) {

        CommentResponse.CreateComment createResult = commentService.createComment(postIdx, writerId, createCommentDto);
        if (createResult.getCommentIdx() == 0) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(createResult);
    }

    @DeleteMapping("/{commentIdx}/delete/{writerId}")
    public ResponseEntity<?> deleteComment(@PathVariable Map<String, String> pathVarsMap) {

        if (!commentService.deleteComment(pathVarsMap)) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/find")
    public ResponseEntity<CommentResponse.CommentList> findComments(@PathVariable Long postIdx,
                                                                    @RequestParam(name = "scroll-size") int scrollSize,
                                                                    @RequestParam(name = "last-commentidx", required = false) Long lastCommentIdx) {

        CommentRequest.FindComments request = new CommentRequest.FindComments(postIdx, scrollSize, lastCommentIdx);

        return ResponseEntity.ok(commentService.findComments(request));
    }
}
