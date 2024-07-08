package com.project.danstagram.domain.post.controller;

import com.project.danstagram.domain.post.dto.*;
import com.project.danstagram.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/p")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostResponse.CreatePost> createPost(@RequestPart("body") PostRequest.CreatePost createPostDto,
                                                              @RequestPart("images") List<MultipartFile> imageFiles) throws IOException {

        // post 저장 & postImage 저장
        PostResponse.CreatePost createResult = postService.createPost(createPostDto, imageFiles);
        if (createResult.postIdx() == 0) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(createResult);
    }

    @GetMapping("/{postIdx}/find")
    public ResponseEntity<PostResponse.FindPost> findPost(@PathVariable Long postIdx) {
        return ResponseEntity.ok(postService.findPost(postIdx));
    }

    @PatchMapping("/{postIdx}/update")
    public ResponseEntity<PostResponse.UpdatePost> updatePost(@PathVariable Long postIdx,
                                                              @RequestBody PostRequest.UpdatePost updatePostDto) {

        return ResponseEntity.ok(postService.updatePost(postIdx, updatePostDto));
    }

    @GetMapping("/profile/{memberId}")
    public ResponseEntity<PostResponse.PostListForProfile> findPostForProfile(@PathVariable String memberId,
                                                                              @RequestParam(name = "scroll-size") int scrollSize,
                                                                              @RequestParam(name = "last-postidx", required = false) Long lastPostIdx) {

        PostRequest.FindPostForProfile request = new PostRequest.FindPostForProfile(memberId, scrollSize, lastPostIdx);

        return ResponseEntity.ok(postService.findPostForProfile(request));
    }

    @PatchMapping("/{postIdx}/update/delete-status")
    public ResponseEntity<PostResponse.UpdatePostDeleteStatus> updatePostDeleteStatus(@PathVariable Long postIdx,
                                                                                     @RequestBody PostRequest.UpdatePostDeleteStatus updatePostDeleteStatus) {

        updatePostDeleteStatus.setPostIdx(postIdx);

        return ResponseEntity.ok(postService.updatePostDeleteStatus(updatePostDeleteStatus));
    }
}
